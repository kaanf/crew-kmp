@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.kaanf.game.data.repository

import com.kaanf.core.data.networking.ConnectivityObserver
import com.kaanf.core.data.networking.JwtTokenInspector
import com.kaanf.core.data.networking.SessionRefresher
import com.kaanf.core.data.networking.UrlConstants
import com.kaanf.core.domain.logging.CrewLogger
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.game.data.dto.SocketEnvelopeDto
import com.kaanf.game.data.lifecycle.AppLifecycleObserver
import com.kaanf.game.data.mappers.toDomain
import com.kaanf.game.data.network.ConnectionErrorHandler
import com.kaanf.game.domain.event.EventConnectionClient
import com.kaanf.game.domain.model.GameConnectionState
import com.kaanf.game.domain.model.GameSocketMessage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.timeout
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class EventConnectionClientImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage,
    private val sessionRefresher: SessionRefresher,
    private val connectionErrorHandler: ConnectionErrorHandler,
    private val logger: CrewLogger,
    connectivityObserver: ConnectivityObserver,
    appLifecycleObserver: AppLifecycleObserver,
) : EventConnectionClient {
    private val json = Json { ignoreUnknownKeys = true }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val streamsLock = Mutex()
    private val streams = mutableMapOf<String, Stream>()

    // Cihaz ağ durumu; kısa flap'leri (ör. hücre↔wifi geçişi) yutmak için debounce'lu.
    private val isConnected = connectivityObserver.isConnected
        .debounce(1.seconds)
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000L), initialValue = false)

    // App ön planda mı? Background'a düşünce soketi proaktif kapatmak için gate'e girer.
    // Hızlı lock/unlock toggle'larını yut: her gate değişimi flatMapLatest'i yeniden kurup
    // backoff sayacını sıfırlar; kısa titreşimleri debounce'layarak gereksiz reconnect/refresh
    // thrash'ini önle.
    private val isInForeground = appLifecycleObserver.isInForeground
        .debounce(FOREGROUND_DEBOUNCE_MS)
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000L), initialValue = false)

    private class Stream(
        val signals: SharedFlow<SocketSignal>,
        val connectionState: StateFlow<GameConnectionState>,
        val lastConnected: StateFlow<GameSocketMessage.Connected?>,
        val activeAnnouncement: StateFlow<TimedAnnouncement?>,
    )

    /** Süresi dolmamış duyuru; geç aboneye kalan süreyle replay'lenir. */
    private class TimedAnnouncement(
        val message: GameSocketMessage.Announcement,
        val expiresAtMillis: Long,
    ) {
        /** Kalan süre bittiyse null; aksi halde geri sayım kaldığı yerden sürsün diye kırpılmış kopya. */
        fun remainingOrNull(nowMillis: Long): GameSocketMessage.Announcement? {
            val remainingSeconds = ((expiresAtMillis - nowMillis) / 1000).toInt()
            return if (remainingSeconds > 0) {
                message.copy(durationSeconds = remainingSeconds)
            } else {
                null
            }
        }
    }

    // Soketin tek kaynağı: hem mesajlar hem de bağlantı durumu aynı paylaşılan akıştan
    // türetilir. Böylece observeEvents veya observeConnectionState'ten *hangisi* collect
    // edilirse edilsin soket canlanır; durum, mesaj pipeline'ına parazitik bağlı değildir.
    private sealed interface SocketSignal {
        data class Update(val state: GameConnectionState) : SocketSignal
        data class Message(val message: GameSocketMessage) : SocketSignal
    }

    override fun observeEvents(eventId: String): Flow<GameSocketMessage> = flow {
        val stream = getOrCreate(eventId)
        // CONNECTED app-bar stats'ının (skor/W-L/history) TEK kaynağı ve akış replay'siz:
        // canlı sokete sonradan abone olan VM (ör. graph'tan çıkıp 5 sn içinde yeniden giriş)
        // onu kaçırır, stats sıfır kalırdı. Son CONNECTED aboneliğe replay'lenir; handler
        // idempotent olduğundan nadir çift teslim zararsız.
        emitAll(
            stream.signals
                .onSubscription {
                    stream.lastConnected.value?.let { emit(SocketSignal.Message(it)) }
                    // Duyuru da replay'lenir: soket ayaktayken (graph'tan 5 sn içinde çıkıp
                    // dönmek gibi) yeni abone ANNOUNCEMENT'ı kaçırır ve chip kaybolurdu.
                    // Soket koptuğunda gerek yok; backend aktif duyuruyu CONNECTED'tan hemen
                    // sonra kalan süreyle zaten tekrar yolluyor.
                    stream.activeAnnouncement.value
                        ?.remainingOrNull(Clock.System.now().toEpochMilliseconds())
                        ?.let { emit(SocketSignal.Message(it)) }
                }
                .filterIsInstance<SocketSignal.Message>()
                .map { it.message },
        )
    }

    override fun observeConnectionState(eventId: String): Flow<GameConnectionState> = flow {
        val stream = getOrCreate(eventId)
        // Durum yan-kanaldan (onEach) yürür; ek olarak signals'ı değer yaymadan collect
        // ederek observeConnectionState'in tek başına da soketi ayakta tutmasını korur.
        emitAll(
            merge(
                stream.connectionState,
                stream.signals.transform<SocketSignal, GameConnectionState> {},
            ),
        )
    }

    private suspend fun getOrCreate(eventId: String): Stream = streamsLock.withLock {
        streams.getOrPut(eventId) { createStream(eventId) }
    }

    private fun createStream(eventId: String): Stream {
        lateinit var stream: Stream
        // Bağlantı durumu yan-kanaldan yürütülür: signals'ı *kim* collect ederse etsin
        // (ör. lobi yalnız event'leri dinlerken) güncellenir, böylece CONNECTED kaçmaz ve
        // ekran geçişinde "Bağlanılıyor"da takılı kalmaz. StateFlow son değeri saklar.
        val connectionState = MutableStateFlow<GameConnectionState>(GameConnectionState.Connecting)
        // Son CONNECTED snapshot'ı: observeEvents geç aboneye bunu replay'ler (üstte açıklandı).
        val lastConnected = MutableStateFlow<GameSocketMessage.Connected?>(null)
        // Süreli duyuru; aynı sebeple geç aboneye kalan süreyle replay'lenir.
        val activeAnnouncement = MutableStateFlow<TimedAnnouncement?>(null)

        // Soketin yaşam döngüsü auth + ağ + foreground kapısına bağlı. Kapı her açıldığında
        // flatMapLatest yeni bir bağlantı kurar; bu da backoff sayacını 0'a çekerek ağ/ön plan
        // dönüşünde anında reconnect sağlar. Background'da soket proaktif kapanır (OS'a karşı
        // savaşmayız); ön plana dönünce reconnect + snapshot reconcile fazı toparlar.
        val signals = combine(
            sessionStorage.observeAuthInfo().map { it != null },
            isConnected,
            isInForeground,
        ) { authenticated, connected, foreground ->
            ConnectionGate(authenticated, connected, foreground)
        }
            .distinctUntilChanged()
            .flatMapLatest { gate ->
                when {
                    !gate.authenticated -> disconnectedSignal(REASON_UNAUTHENTICATED)
                    !gate.foreground -> disconnectedSignal(REASON_BACKGROUND)
                    !gate.connected -> disconnectedSignal(REASON_NO_NETWORK)
                    else -> connectedSignalFlow(eventId)
                }
            }
            // Son çare: connectedSignalFlow kendi hatalarını zaten ele alıyor; buraya ancak
            // beklenmedik bir pipeline hatası düşer. Sessiz yutma — logla (#5).
            .catch { cause -> logger.error("Event socket stream failed: event=$eventId", cause) }
            // Durum güncellemesini paylaşılan upstream'e yerleştir; her abone için değil,
            // upstream başına bir kez çalışır ve connectionState'i abonesiz de besler.
            .onEach { signal ->
                when {
                    signal is SocketSignal.Update -> connectionState.value = signal.state
                    signal is SocketSignal.Message && signal.message is GameSocketMessage.Connected ->
                        lastConnected.value = signal.message

                    signal is SocketSignal.Message && signal.message is GameSocketMessage.Announcement ->
                        activeAnnouncement.value = signal.message.durationSeconds
                            ?.let { seconds ->
                                TimedAnnouncement(
                                    message = signal.message,
                                    expiresAtMillis = Clock.System.now().toEpochMilliseconds() +
                                        seconds * 1000L,
                                )
                            }
                }
            }
            // Aboneliği kalmayan stream'i map'ten düşür; aksi halde her distinct event kalıcı
            // birikir (#4). shareIn upstream'i (WhileSubscribed) iptal edince burası tetiklenir.
            .onCompletion {
                scope.launch {
                    streamsLock.withLock {
                        if (streams[eventId] === stream) {
                            streams.remove(eventId)
                        }
                    }
                }
            }
            .shareIn(scope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L))

        stream = Stream(
            signals = signals,
            connectionState = connectionState,
            lastConnected = lastConnected,
            activeAnnouncement = activeAnnouncement,
        )
        return stream
    }

    private fun disconnectedSignal(reason: String): Flow<SocketSignal> =
        flowOf(SocketSignal.Update(GameConnectionState.Disconnected(code = null, reason = reason)))

    private fun connectedSignalFlow(eventId: String): Flow<SocketSignal> = flow {
        var attempt = 0L
        var everConnected = false
        var authRefreshAttempted = false
        while (true) {
            emit(
                SocketSignal.Update(
                    if (attempt == 0L && !everConnected) GameConnectionState.Connecting else GameConnectionState.Reconnecting,
                ),
            )

            // Proaktif yenileme: access token (wall-clock + marj) dolduysa el sıkışmayı denemeden
            // yenile. Backend bayat token'lı handshake'i upgrade'den ÖNCE 401 ile reddediyor; o
            // 401'i Ktor/Darwin platformlar arası güvenilir okuyamadığımız için doomed denemeye
            // hiç girmemek en sağlamı (asıl 30 dk lock senaryosunu burası kapatır). Refresh
            // otoritesi ortak SessionRefresher (WS+REST tek mutex + tek storage → tek-uçuş).
            // Guard yok: başarılı yenileme sonrası token artık bayat değil, sonraki turda tekrar
            // tetiklenmez; geçici (ağ) hatada backoff'la tekrar dener — token'sız bağlanmak imkânsız.
            if (isAccessTokenExpired()) {
                when (refreshSession()) {
                    RefreshOutcome.Refreshed -> Unit
                    RefreshOutcome.AuthCleared -> {
                        emit(SocketSignal.Update(GameConnectionState.Disconnected(null, REASON_UNAUTHENTICATED)))
                        return@flow
                    }
                    RefreshOutcome.Transient -> {
                        delay(backoffMillis(attempt))
                        attempt++
                        continue
                    }
                }
            }

            var connectedThisAttempt = false
            try {
                rawSocketFlow(eventId).collect { message ->
                    if (message is GameSocketMessage.Connected) {
                        // Sağlıklı bağlantı kuruldu: reaktif refresh hakkı tazelenir ve backoff
                        // sıfırlanır — yoksa akşam boyu her kopuş kalıcı olarak cezayı büyütür.
                        connectedThisAttempt = true
                        everConnected = true
                        authRefreshAttempted = false
                        attempt = 0L
                        emit(SocketSignal.Update(GameConnectionState.Connected))
                    }
                    emit(SocketSignal.Message(message))
                }
                return@flow
            } catch (e: CancellationException) {
                throw e
            } catch (e: TerminalSocketException) {
                // Sunucunun "tekrar deneme" dediği temiz kapanış (1008 check-in yok / user yok,
                // 1003 geçersiz eventId vb.). Auth artık buraya düşmez (handshake'te 401), bu
                // yüzden burada yenileme yok — bunlar gerçekten terminal.
                emit(SocketSignal.Update(GameConnectionState.Disconnected(e.code, e.message)))
                return@flow
            } catch (e: Throwable) {
                // Reaktif kurtarma: proaktif kontrol kaçırdıysa (ör. cihaz saati kayması) ve hiç
                // bağlanamadan düştüysek, sebebi (handshake 401'ini okuyamasak bile) auth varsayıp
                // bu turda bir kez yenile. Tespit status'a değil guard'a bağlı (status iOS'ta
                // okunamıyor). Yenileme başarılıysa taze token'la anında tekrar dene.
                if (!authRefreshAttempted && !connectedThisAttempt) {
                    authRefreshAttempted = true
                    when (refreshSession()) {
                        RefreshOutcome.Refreshed -> continue
                        RefreshOutcome.AuthCleared -> {
                            emit(SocketSignal.Update(GameConnectionState.Disconnected(null, REASON_UNAUTHENTICATED)))
                            return@flow
                        }
                        RefreshOutcome.Transient -> Unit // ağ hatası: aşağıdaki retriable backoff'a düş
                    }
                }
                if (e is RetryableSocketException || connectionErrorHandler.isRetriableError(e)) {
                    // Temiz ama geçici kapanış ya da geçici sayılan ham transport hatası → backoff.
                    delay(backoffMillis(attempt))
                    attempt++
                } else {
                    // Beklenmedik/kalıcı hata: sonsuz döngüye girme, logla ve kopuk bırak (#5).
                    logger.error("Event socket gave up: event=$eventId", e)
                    emit(SocketSignal.Update(GameConnectionState.Disconnected(null, e.message)))
                    return@flow
                }
            }
        }
    }

    // Access token dolmuş mu? Wall-clock + marj kullanır. kotlinx monotonic saat derin uykuda
    // ilerlemediği için (tam da 30 dk lock senaryosunu kaçırırdı) bilerek wall-clock; tek zayıflığı
    // kullanıcının saatini elle yanlış kurması — o nadir durumda reaktif yol (yukarıda) backstop.
    private suspend fun isAccessTokenExpired(): Boolean {
        val token = sessionStorage.observeAuthInfo().firstOrNull()?.accessToken ?: return false
        val expiresAt = JwtTokenInspector.expiresAtEpochSeconds(token) ?: return false
        return Clock.System.now().epochSeconds >= expiresAt - TOKEN_EXPIRY_MARGIN_SECONDS
    }

    private suspend fun refreshSession(): RefreshOutcome {
        val refreshed = sessionRefresher.refresh(httpClient)
        return when {
            refreshed != null -> RefreshOutcome.Refreshed
            // Refresh 401/403 alınca SessionRefresher storage'ı temizler → terminal, re-login.
            sessionStorage.observeAuthInfo().firstOrNull() == null -> RefreshOutcome.AuthCleared
            // Token hâlâ duruyor: yenileme geçici (ağ) sebeple başarısız → yeniden denenebilir.
            else -> RefreshOutcome.Transient
        }
    }

    private enum class RefreshOutcome { Refreshed, AuthCleared, Transient }

    private fun rawSocketFlow(eventId: String): Flow<GameSocketMessage> = flow {
        val accessToken = sessionStorage.observeAuthInfo().firstOrNull()?.accessToken
            ?: throw TerminalSocketException(code = null, message = REASON_UNAUTHENTICATED)

        // Soket açıkken timeout'lar bilerek INFINITE (keepalive otoritesi backend); ama bu
        // el sıkışmayı da kapsıyordu: resume sonrası kara deliğe düşen bir connect denemesi
        // sonsuza dek askıda kalıp ekranı "Reconnecting"de kilitliyordu. Handshake'e ayrı,
        // sonlu bir süre tanı; aşımı retriable say ki backoff döngüsü ilerlesin.
        // (TimeoutCancellationException bir CancellationException'dır — dönüştürülmezse
        // connectedSignalFlow'un rethrow dalına düşüp akışı sessizce öldürür.)
        val session = try {
            withTimeout(CONNECT_TIMEOUT_MS) {
                httpClient.webSocketSession {
                    url(UrlConstants.BASE_URL_WS)
                    parameter("eventId", eventId)
                    header(HttpHeaders.Authorization, "Bearer $accessToken")
                    timeout {
                        socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                        requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            throw RetryableSocketException(code = null, message = REASON_CONNECT_TIMEOUT)
        }

        try {
            while (true) {
                // Half-open watchdog: backend 30 sn'de bir görünür HEARTBEAT frame'i basar
                // (protokol PING'leri engine'de yutulur, uygulamaya hiç düşmez). Bu pencere
                // boyunca tek frame gelmediyse hat ölü kabul edilir — server bizi çoktan
                // registry'den silmiş olabilirken client sonsuza dek "Connected" sanıyordu.
                // (emit, withTimeout bloğunun dışında: flow invariant'ı emisyona izin vermez.)
                val received = withTimeoutOrNull(HEARTBEAT_WATCHDOG_MS) {
                    session.incoming.receiveCatching()
                } ?: throw RetryableSocketException(code = null, message = REASON_HEARTBEAT_TIMEOUT)

                val frame = received.getOrNull() ?: break

                if (frame !is Frame.Text) continue

                val envelope = runCatching {
                    json.decodeFromString<SocketEnvelopeDto>(frame.readText())
                }.getOrNull() ?: continue

                // Heartbeat'in tek işi watchdog'u beslemek; domain'e sızdırma — aksi halde
                // VM'de socketEpoch'u artırıp uçuştaki snapshot reconcile'ı bayatlatır.
                if (envelope.type == TYPE_HEARTBEAT) continue

                emit(envelope.toDomain(json))
            }

            throw closeException(session.closeReason.await())
        } finally {
            // Kapanış iptal altında da tamamlanmalı: close() suspend olduğundan iptal edilmiş
            // coroutine'de Close frame gitmeden fırlar ve server'da ping-timeout'a kadar
            // yaşayan zombi oturumlar birikirdi.
            withContext(NonCancellable) { session.close() }
        }
    }

    private fun closeException(reason: CloseReason?): Exception {
        val code = reason?.code?.toInt()
        val message = reason?.message?.takeIf { it.isNotBlank() }
        return when (reason?.knownReason) {
            CloseReason.Codes.VIOLATED_POLICY,
            CloseReason.Codes.CANNOT_ACCEPT,
            -> TerminalSocketException(code = code, message = message)

            else -> RetryableSocketException(code = code, message = message)
        }
    }

    private fun backoffMillis(attempt: Long): Long {
        val exp = attempt.coerceIn(0, MAX_BACKOFF_EXP).toInt()
        val base = minOf(BASE_BACKOFF_MS shl exp, MAX_BACKOFF_MS)
        // Equal jitter: deterministik backoff, mekânın Wi-Fi'ı flap'leyince salondaki tüm
        // client'ları aynı saniyede geri getirip backend'e senkron reconnect dalgası vurdurur.
        // Yarısı garanti bekleme, yarısı rastgele — denemeler zamana yayılır.
        return base / 2 + Random.nextLong(base / 2 + 1)
    }

    private data class ConnectionGate(
        val authenticated: Boolean,
        val connected: Boolean,
        val foreground: Boolean,
    )

    private companion object {
        const val BASE_BACKOFF_MS = 1_000L
        const val MAX_BACKOFF_MS = 15_000L
        const val MAX_BACKOFF_EXP = 5L

        /** Proaktif yenileme marjı: exp'ten bu kadar saniye önce dolmuş say (clock skew tamponu). */
        const val TOKEN_EXPIRY_MARGIN_SECONDS = 60L

        /** Lock/unlock thrash'ini yutmak için ön plan sinyalinin debounce penceresi. */
        const val FOREGROUND_DEBOUNCE_MS = 300L

        // Beklenen kopuş sebepleri domain'de tek kaynaktan tanımlı (Disconnected.isError bunlara bakar);
        // burada referans alıyoruz ki data ↔ domain string'leri birbirinden sapmasın.
        val REASON_UNAUTHENTICATED = GameConnectionState.REASON_UNAUTHENTICATED
        val REASON_BACKGROUND = GameConnectionState.REASON_BACKGROUND
        val REASON_NO_NETWORK = GameConnectionState.REASON_NO_NETWORK
        const val REASON_CONNECT_TIMEOUT = "connect_timeout"
        const val REASON_HEARTBEAT_TIMEOUT = "heartbeat_timeout"

        const val TYPE_HEARTBEAT = "HEARTBEAT"

        /** WS el sıkışması için üst sınır; aşımı retriable kopuş sayılır. */
        const val CONNECT_TIMEOUT_MS = 10_000L

        /**
         * Backend HEARTBEAT aralığının (30 sn) 2x'i + pay: iki ardışık heartbeat'i de
         * kaçırdıysak hat half-open kabul edilir ve yeniden bağlanılır.
         */
        const val HEARTBEAT_WATCHDOG_MS = 75_000L
    }
}
