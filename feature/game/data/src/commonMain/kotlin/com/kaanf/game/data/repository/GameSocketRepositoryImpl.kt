package com.kaanf.game.data.repository

import com.kaanf.core.data.networking.UrlConstants
import com.kaanf.game.data.dto.SocketEnvelopeDto
import com.kaanf.game.data.mappers.toDomain
import com.kaanf.game.domain.model.GameConnectionState
import com.kaanf.game.domain.model.GameSocketMessage
import com.kaanf.game.domain.repository.GameSocketRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

class GameSocketRepositoryImpl(
    private val httpClient: HttpClient,
) : GameSocketRepository {
    private val json = Json { ignoreUnknownKeys = true }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val streamsLock = Mutex()
    private val streams = mutableMapOf<String, Stream>()

    private class Stream(
        val messages: Flow<GameSocketMessage>,
        val connectionState: StateFlow<GameConnectionState>,
    )

    override fun observeEvents(eventId: String): Flow<GameSocketMessage> = flow {
        emitAll(getOrCreate(eventId).messages)
    }

    override fun observeConnectionState(eventId: String): Flow<GameConnectionState> = flow {
        emitAll(getOrCreate(eventId).connectionState)
    }

    private suspend fun getOrCreate(eventId: String): Stream = streamsLock.withLock {
        streams.getOrPut(eventId) { createStream(eventId) }
    }

    private fun createStream(eventId: String): Stream {
        val state = MutableStateFlow<GameConnectionState>(GameConnectionState.Connecting)

        val messages = rawSocketFlow(eventId)
            .onStart { state.value = GameConnectionState.Connecting }
            .onEach { message ->
                if (message is GameSocketMessage.Connected) {
                    state.value = GameConnectionState.Connected
                }
            }
            .retryWhen { cause, attempt ->
                if (cause is TerminalSocketException) {
                    state.value = GameConnectionState.Disconnected(cause.code, cause.message)
                    false
                } else {
                    state.value = GameConnectionState.Reconnecting
                    delay(backoffMillis(attempt))
                    true
                }
            }
            .catch { }
            .shareIn(scope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L))

        return Stream(messages = messages, connectionState = state.asStateFlow())
    }

    private fun rawSocketFlow(eventId: String): Flow<GameSocketMessage> = flow {
        val session = httpClient.webSocketSession {
            url("${UrlConstants.BASE_URL_WS}/ws/events")
            parameter("eventId", eventId)
        }

        try {
            for (frame in session.incoming) {
                if (frame !is Frame.Text) continue

                val envelope = runCatching {
                    json.decodeFromString<SocketEnvelopeDto>(frame.readText())
                }.getOrNull() ?: continue

                emit(envelope.toDomain(json))
            }

            throw closeException(session.closeReason.await())
        } finally {
            session.close()
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
        return minOf(BASE_BACKOFF_MS shl exp, MAX_BACKOFF_MS)
    }

    private companion object {
        const val BASE_BACKOFF_MS = 1_000L
        const val MAX_BACKOFF_MS = 15_000L
        const val MAX_BACKOFF_EXP = 5L
    }
}
