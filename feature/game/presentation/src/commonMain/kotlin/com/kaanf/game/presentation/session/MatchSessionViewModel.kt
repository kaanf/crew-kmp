package com.kaanf.game.presentation.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.game.domain.model.GameConnectionState
import com.kaanf.game.domain.model.GameSocketMessage
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.MatchSnapshot
import com.kaanf.game.domain.event.EventConnectionClient
import com.kaanf.game.domain.repository.MatchRepository
import com.kaanf.game.presentation.session.MatchPhase.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MatchSessionViewModel(
    private val eventConnectionClient: EventConnectionClient,
    private val matchRepository: MatchRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId: String = savedStateHandle.get<String>("eventId").orEmpty()

    private val eventChannel = Channel<MatchSessionEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(MatchSessionState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        loadMyParticipant()
        observeConnectionState()
        subscribeToEvents()
    }

    // region Socket
    // Her işlenen soket mesajında artar. Reconnect'te snapshot çekerken canlı bir event
    // geldiyse (epoch değişmişse) snapshot bayatlamıştır; push-only soket reconnect sonrası
    // yalnız yeni event verdiği için canlı mesaj her zaman snapshot'tan tazedir. Bu, ordinal
    // tabanlı guard'ın aksine görev-reddi gibi geri geçişlerde de doğru çalışır.
    private var socketEpoch = 0

    private fun observeConnectionState() {
        eventConnectionClient
            .observeConnectionState(eventId)
            .onEach { connectionState ->
                _state.update {
                    it.copy(
                        connectionState = connectionState
                    )
                }
                // Her (yeniden) bağlanmada sunucu doğrusuyla uzlaş: kopukken kaçırılan
                // push'lara rağmen faz snapshot'tan kurulur (ilk bağlantı dahil).
                if (connectionState is GameConnectionState.Connected) {
                    reconcileFromSnapshot()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun reconcileFromSnapshot() {
        val epochAtStart = socketEpoch
        viewModelScope.launch {
            matchRepository.getMatchSnapshot(eventId)
                .onSuccess { snapshot ->
                    // Snapshot uçarken canlı event geldiyse onu esas al, snapshot'ı at.
                    if (socketEpoch != epochAtStart) return@onSuccess
                    if (snapshot == null) onMatchEnded() else applySnapshot(snapshot)
                }
            // onFailure: geçici hata; mevcut state korunur, soket akmaya devam eder.
        }
    }

    private fun applySnapshot(snapshot: MatchSnapshot) {
        val phase = snapshot.toMatchPhase()
        // Karar: server kazanır. Faz tamamen snapshot'tan kurulur, in-flight loading
        // bayrakları ve davet sheet'leri sıfırlanır (zaten kopuktun).
        _state.update {
            it.copy(
                phase = phase,
                currentUserId = it.currentUserId ?: snapshot.me.userId,
                matchId = snapshot.matchId,
                opponentFullName = snapshot.opponent.fullName,
                amIWinner = snapshot.amIWinnerOrNull(),
                activeTask = snapshot.task ?: it.activeTask,
                incomingInvite = null,
                isRespondingToInvite = false,
                showMatchRequestSheet = false,
                isSendingInvite = false,
                showOutgoingInviteSheet = false,
                outgoingOpponentName = null,
                errorMessage = null,
            )
        }
        // Snapshot taşımayan veriler: kazananın görev listesi / puan tablosu.
        when (phase) {
            is MatchPhase.WinnerPicks -> loadTasks()
            is MatchPhase.Scoreboard -> loadScoreboard()
            else -> Unit
        }
    }

    private fun onMatchEnded() {
        val current = _state.value
        // İlk bağlantıda zaten Idle'daysak (maç hiç başlamadıysa) sessiz kal.
        if (current.phase == MatchPhase.Idle && current.matchId == null) return
        // Kopukken maç bitti/iptal oldu: Idle'a sıfırla ve kullanıcıyı bilgilendir.
        _state.update {
            MatchSessionState(
                connectionState = it.connectionState,
                matchQrToken = it.matchQrToken,
                currentUserId = it.currentUserId,
                errorMessage = MATCH_ENDED_MESSAGE,
            )
        }
    }

    private fun subscribeToEvents() {
        eventConnectionClient.observeEvents(eventId)
            .onEach(::onSocketMessage)
            .catch { error -> _state.update { it.copy(errorMessage = error.message) } }
            .launchIn(viewModelScope)
    }

    private suspend fun onSocketMessage(message: GameSocketMessage) {
        // Canlı event geldi; uçmakta olan bir snapshot reconcile'ı varsa bayatlatır.
        socketEpoch++
        when (message) {
            is GameSocketMessage.MatchInviteReceived -> {
                _state.update {
                    it.copy(
                        incomingInvite = message,
                        showMatchRequestSheet = true,
                    )
                }
            }

            is GameSocketMessage.MatchStarted -> {
                _state.update {
                    it.copy(
                        showMatchRequestSheet = false,
                        incomingInvite = null,
                        showOutgoingInviteSheet = false,
                        outgoingOpponentName = null,
                        matchId = message.matchId,
                        opponentFullName = message.opponentFullName,
                        phase = RpsReady(),
                    )
                }
            }

            is GameSocketMessage.MatchInviteDeclined ->
                _state.update {
                    it.copy(showOutgoingInviteSheet = false, outgoingOpponentName = null)
                }

            is GameSocketMessage.MatchReadyCompleted -> {
                _state.update { it.copy(phase = WhoWon()) }
            }

            is GameSocketMessage.MatchResultReported -> {
                _state.update { state ->
                    val phase = state.phase as? WhoWon ?: return@update state
                    state.copy(
                        phase = phase.copy(
                            opponentClaimedWinnerUserId = message.claimedWinnerUserId,
                        ),
                    )
                }
            }

            is GameSocketMessage.MatchResultConfirmed -> {
                val isWinner = message.winnerUserId == _state.value.currentUserId
                val whoWon = _state.value.phase as? WhoWon
                if (whoWon != null && whoWon.opponentClaimedWinnerUserId == null) {
                    _state.update {
                        it.copy(
                            phase = whoWon.copy(opponentClaimedWinnerUserId = message.winnerUserId),
                        )
                    }
                    delay(RESULT_CONFIRM_REVEAL_DELAY_MS)
                }
                _state.update { it.copy(amIWinner = isWinner) }
                if (isWinner) {
                    _state.update { it.copy(phase = WinnerPicks(isLoading = true)) }
                    loadTasks()
                } else {
                    _state.update { it.copy(phase = LoserWaits) }
                }
            }

            is GameSocketMessage.TaskOffered -> {
                _state.update {
                    it.copy(
                        phase = LoserAccepts(
                            task = GameTask(
                                id = message.taskId,
                                title = message.taskTitle,
                                points = message.taskPoints,
                                categories = message.taskCategories,
                            ),
                        ),
                    )
                }
            }

            is GameSocketMessage.TaskRejected -> {
                _state.update { state ->
                    val phase = state.phase as? WinnerPicks ?: return@update state
                    state.copy(phase = phase.copy(isOffering = false, selectedTaskId = null))
                }
            }

            is GameSocketMessage.TaskStarted -> {
                _state.update { state ->
                    val activeTask = when (val phase = state.phase) {
                        is WinnerPicks -> phase.tasks.firstOrNull { it.id == phase.selectedTaskId }
                        is LoserAccepts -> phase.task
                        else -> state.activeTask
                    }
                    state.copy(
                        activeTask = activeTask,
                        phase = if (state.amIWinner == true) WinnerConfirms() else TaskActive,
                    )
                }
            }

            is GameSocketMessage.TaskFinished -> {
                _state.update { it.copy(phase = Scoreboard(completed = message.completed)) }
                loadScoreboard()
            }

            is GameSocketMessage.GameStarted,
            GameSocketMessage.Connected,
            is GameSocketMessage.Unknown,
            is GameSocketMessage.MatchCancelled,
            is GameSocketMessage.MatchInviteExpired -> Unit
        }
    }

    // endregion

    // region Actions
    fun onAction(action: MatchSessionAction) {
        when (action) {
            MatchSessionAction.OnBackClick -> _state.update { it.copy(showExitConfirmDialog = true) }
            MatchSessionAction.OnExitDismissed -> _state.update { it.copy(showExitConfirmDialog = false) }
            MatchSessionAction.OnExitConfirmed -> onExitConfirmed()
            MatchSessionAction.OnScanClicked -> sendEvent(MatchSessionEvent.NavigateToScanOpponent)
            is MatchSessionAction.OnScanResult -> onScanResult(action.scannedMatchQrToken)
            MatchSessionAction.OnInviteAccepted -> respondToInvite { inviteId ->
                matchRepository.acceptInvite(eventId = eventId, inviteId = inviteId)
            }

            MatchSessionAction.OnInviteDeclined -> respondToInvite { inviteId ->
                matchRepository.declineInvite(eventId = eventId, inviteId = inviteId)
            }

            MatchSessionAction.OnReadyClick -> markReady()
            is MatchSessionAction.OnReportResult -> reportResult(action.won)
            is MatchSessionAction.OnTaskSelected -> onTaskSelected(action.taskId)
            MatchSessionAction.OnSendTaskClick -> offerTask()
            MatchSessionAction.OnAcceptTask -> respondToTaskOffer(accept = true)
            MatchSessionAction.OnRejectTask -> respondToTaskOffer(accept = false)
            is MatchSessionAction.OnConfirmTask -> confirmTask(action.completed)
            MatchSessionAction.OnFinishMatch -> finishMatch()
        }
    }

    private fun confirmTask(completed: Boolean) {
        val matchId = _state.value.matchId ?: return
        val phase = _state.value.phase as? WinnerConfirms ?: return
        if (phase.isConfirming) return
        _state.update { it.copy(phase = phase.copy(isConfirming = true), errorMessage = null) }

        viewModelScope.launch {
            matchRepository.confirmTask(eventId = eventId, matchId = matchId, completed = completed)
                .onFailure { error ->
                    _state.update { state ->
                        val current = state.phase as? WinnerConfirms ?: return@update state
                        state.copy(
                            phase = current.copy(isConfirming = false),
                            errorMessage = error.toString(),
                        )
                    }
                }
            // Başarıda bekleme sürer; geçişi (puan tablosu) TASK_FINISHED soketi yapar.
        }
    }

    private fun loadScoreboard() {
        val matchId = _state.value.matchId ?: return
        viewModelScope.launch {
            matchRepository.getScoreboard(eventId = eventId, matchId = matchId)
                .onSuccess { scoreboard ->
                    _state.update { state ->
                        val phase = state.phase as? Scoreboard ?: return@update state
                        state.copy(
                            phase = phase.copy(isLoading = false, entries = scoreboard.entries),
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { state ->
                        val phase = state.phase as? Scoreboard ?: return@update state
                        state.copy(
                            phase = phase.copy(isLoading = false),
                            errorMessage = error.toString(),
                        )
                    }
                }
        }
    }

    private fun finishMatch() {
        val matchId = _state.value.matchId ?: return
        val phase = _state.value.phase as? Scoreboard ?: return
        if (phase.isFinishing) return
        _state.update { it.copy(phase = phase.copy(isFinishing = true), errorMessage = null) }

        viewModelScope.launch {
            matchRepository.finishMatch(eventId = eventId, matchId = matchId)
                .onSuccess {
                    // Maç bitti; soket push'u yok. Ekranı QR/scan home'una (Idle) sıfırlar,
                    // maça özel state temizlenir, soket bağlantısı ve kimlik korunur.
                    _state.update { current ->
                        MatchSessionState(
                            connectionState = current.connectionState,
                            matchQrToken = current.matchQrToken,
                            currentUserId = current.currentUserId,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { state ->
                        val current = state.phase as? Scoreboard ?: return@update state
                        state.copy(
                            phase = current.copy(isFinishing = false),
                            errorMessage = error.toString(),
                        )
                    }
                }
        }
    }

    private fun respondToTaskOffer(accept: Boolean) {
        val matchId = _state.value.matchId ?: return
        val phase = _state.value.phase as? LoserAccepts ?: return
        if (phase.isResponding) return
        _state.update { it.copy(phase = phase.copy(isResponding = true), errorMessage = null) }

        viewModelScope.launch {
            val result = if (accept) {
                matchRepository.acceptTask(eventId = eventId, matchId = matchId)
            } else {
                matchRepository.rejectTask(eventId = eventId, matchId = matchId)
            }
            result
                .onSuccess {
                    // Reddetmede kaybeden beklemeye döner (kazanan tekrar seçer); kabulde ise
                    // geçişi TASK_STARTED soketi sürer, o yüzden buton loading'de bırakılır.
                    if (!accept) {
                        _state.update { it.copy(phase = LoserWaits) }
                    }
                }
                .onFailure { error ->
                    _state.update { state ->
                        val current = state.phase as? LoserAccepts ?: return@update state
                        state.copy(
                            phase = current.copy(isResponding = false),
                            errorMessage = error.toString(),
                        )
                    }
                }
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            matchRepository.getTasks()
                .onSuccess { tasks ->
                    _state.update { state ->
                        val phase = state.phase as? WinnerPicks ?: return@update state
                        state.copy(phase = phase.copy(isLoading = false, tasks = tasks))
                    }
                }
                .onFailure { error ->
                    _state.update { state ->
                        val phase = state.phase as? WinnerPicks ?: return@update state
                        state.copy(
                            phase = phase.copy(isLoading = false),
                            errorMessage = error.toString(),
                        )
                    }
                }
        }
    }

    private fun onTaskSelected(taskId: String) {
        _state.update { state ->
            val phase = state.phase as? WinnerPicks ?: return@update state
            if (phase.isOffering) return@update state
            state.copy(phase = phase.copy(selectedTaskId = taskId))
        }
    }

    private fun offerTask() {
        val matchId = _state.value.matchId ?: return
        val phase = _state.value.phase as? WinnerPicks ?: return
        val taskId = phase.selectedTaskId ?: return
        if (phase.isOffering) return
        _state.update { it.copy(phase = phase.copy(isOffering = true), errorMessage = null) }

        viewModelScope.launch {
            matchRepository.offerTask(eventId = eventId, matchId = matchId, taskId = taskId)
                .onSuccess {
                    // Görev sunuldu; kazanan WinnerPicks'te kalır, buton loading'de kalarak
                    // kaybedenin yanıtını bekler. Geçiş TASK_STARTED / TASK_REJECTED soketiyle sürülür.
                }
                .onFailure { error ->
                    _state.update { state ->
                        val current = state.phase as? WinnerPicks ?: return@update state
                        state.copy(
                            phase = current.copy(isOffering = false),
                            errorMessage = error.toString(),
                        )
                    }
                }
        }
    }

    private fun markReady() {
        val matchId = _state.value.matchId ?: return
        val phase = _state.value.phase as? RpsReady ?: return
        if (phase.isMarkingReady) return
        _state.update { it.copy(phase = phase.copy(isMarkingReady = true), errorMessage = null) }

        viewModelScope.launch {
            matchRepository.markReady(eventId = eventId, matchId = matchId)
                .onFailure { error ->
                    _state.update { state ->
                        val current = state.phase as? RpsReady ?: return@update state
                        state.copy(
                            phase = current.copy(isMarkingReady = false),
                            errorMessage = error.toString(),
                        )
                    }
                }
            // Başarıda bekleme sürer; geçişi MATCH_READY_COMPLETED soketi yapar.
        }
    }

    private fun reportResult(won: Boolean) {
        val matchId = _state.value.matchId ?: return
        val phase = _state.value.phase as? WhoWon ?: return
        if (phase.isReporting) return
        _state.update {
            it.copy(
                phase = phase.copy(isReporting = true, myResultClaimWon = won),
                errorMessage = null,
            )
        }

        viewModelScope.launch {
            // Kullanıcı kendi avatarının satıra düşme animasyonunu görsün diye isteği
            // kısa bir süre geciktiriyoruz; aksi halde ikinci tıklayanda MATCH_RESULT_CONFIRMED
            // hemen dönüp ekran animasyon görünmeden bir sonraki faza geçiyor.
            delay(RESULT_REPORT_DELAY_MS)
            matchRepository.reportResult(eventId = eventId, matchId = matchId, won = won)
                .onFailure { error ->
                    _state.update { state ->
                        val current = state.phase as? WhoWon ?: return@update state
                        state.copy(
                            phase = current.copy(isReporting = false, myResultClaimWon = null),
                            errorMessage = error.toString(),
                        )
                    }
                }
            // Başarıda bekleme sürer; geçişi MATCH_RESULT_CONFIRMED soketi yapar.
        }
    }

    private fun respondToInvite(call: suspend (inviteId: String) -> EmptyResult<DataError.Remote>) {
        val inviteId = _state.value.incomingInvite?.inviteId ?: return
        if (_state.value.isRespondingToInvite) return
        _state.update { it.copy(isRespondingToInvite = true, errorMessage = null) }

        viewModelScope.launch {
            call(inviteId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isRespondingToInvite = false,
                            showMatchRequestSheet = false,
                            incomingInvite = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isRespondingToInvite = false,
                            errorMessage = error.toString(),
                        )
                    }
                }
        }
    }

    private fun onScanResult(scannedMatchQrToken: String) {
        if (_state.value.isSendingInvite) return
        _state.update { it.copy(isSendingInvite = true, errorMessage = null) }

        viewModelScope.launch {
            matchRepository.sendInvite(eventId = eventId, scannedMatchQrToken = scannedMatchQrToken)
                .onSuccess { invite ->
                    _state.update {
                        it.copy(
                            isSendingInvite = false,
                            outgoingOpponentName = invite.toFullName,
                            showOutgoingInviteSheet = true,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSendingInvite = false,
                            errorMessage = error.toString(),
                        )
                    }
                }
        }
    }

    private fun loadMyParticipant() {
        viewModelScope.launch {
            matchRepository.getMyParticipant(eventId)
                .onSuccess { participant ->
                    _state.update {
                        it.copy(
                            matchQrToken = participant.matchQrToken,
                            currentUserId = participant.userId,
                        )
                    }
                }
        }
    }

    private fun onExitConfirmed() {
        _state.update { it.copy(showExitConfirmDialog = false) }
        sendEvent(MatchSessionEvent.NavigateToDashboard)
    }

    private fun sendEvent(event: MatchSessionEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }
    // endregion

    private companion object {
        const val RESULT_REPORT_DELAY_MS = 100L
        const val RESULT_CONFIRM_REVEAL_DELAY_MS = 900L
        const val MATCH_ENDED_MESSAGE = "Maç sonlandı."
    }
}
