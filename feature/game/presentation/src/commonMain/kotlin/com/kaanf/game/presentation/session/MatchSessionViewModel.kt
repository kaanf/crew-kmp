package com.kaanf.game.presentation.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.designsystem.component.avatar.avatarPaletteColor
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import com.kaanf.core.domain.logging.CrewLogger
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.core.domain.repository.UserRepository
import com.kaanf.core.presentation.model.LobbyMember
import com.kaanf.core.presentation.model.UserAvatar
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.core.presentation.util.UIText
import com.kaanf.game.domain.model.GameConnectionState
import com.kaanf.game.domain.model.GameSocketMessage
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.MatchSnapshot
import com.kaanf.game.domain.event.EventConnectionClient
import com.kaanf.game.domain.repository.MatchRepository
import com.kaanf.game.presentation.session.MatchPhase.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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
import kotlinx.datetime.Instant
import kotlin.time.Clock
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_connection_lost_description
import crew.feature.game.presentation.generated.resources.match_disputed_description
import crew.feature.game.presentation.generated.resources.match_disputed_title
import crew.feature.game.presentation.generated.resources.match_ended_description
import crew.feature.game.presentation.generated.resources.match_ended_title
import crew.feature.game.presentation.generated.resources.match_connection_lost_title
import crew.feature.game.presentation.generated.resources.match_invite_declined_description
import crew.feature.game.presentation.generated.resources.match_invite_declined_title
import crew.feature.game.presentation.generated.resources.match_invite_expired_description
import crew.feature.game.presentation.generated.resources.match_invite_expired_title
import crew.feature.game.presentation.generated.resources.match_invite_failed_description
import crew.feature.game.presentation.generated.resources.match_invite_failed_title

class MatchSessionViewModel(
    private val eventConnectionClient: EventConnectionClient,
    private val matchRepository: MatchRepository,
    private val userRepository: UserRepository,
    private val snackbarController: SnackbarController,
    private val logger: CrewLogger,
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
        observeCurrentUser()
        observeConnectionState()
        subscribeToEvents()
    }

    private fun observeCurrentUser() {
        userRepository.observeCurrentUser()
            .onEach { user ->
                _state.update {
                    it.copy(
                        currentUserPhotoUrl = user?.profilePictureUrl,
                        currentUserName = user?.fullName,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // region Socket
    private var socketEpoch = 0

    private fun observeConnectionState() {
        eventConnectionClient
            .observeConnectionState(eventId)
            .onEach { connectionState ->
                val previous = _state.value.connectionState
                _state.update {
                    it.copy(
                        connectionState = connectionState
                    )
                }

                if (connectionState is GameConnectionState.Connected) {
                    reconcileFromSnapshot()
                }

                if (connectionState is GameConnectionState.Disconnected &&
                    connectionState.isError &&
                    previous != connectionState
                ) {
                    snackbarController.show(connectionLostSnackbar())
                }
            }
            .launchIn(viewModelScope)
    }

    private fun connectionLostSnackbar() = SnackbarMessage(
        title = UIText.Resource(Res.string.match_connection_lost_title),
        description = UIText.Resource(Res.string.match_connection_lost_description),
        variant = SnackbarVariant.Error,
    )

    private fun inviteDeclinedSnackbar() = SnackbarMessage(
        title = UIText.Resource(Res.string.match_invite_declined_title),
        description = UIText.Resource(Res.string.match_invite_declined_description),
        variant = SnackbarVariant.Warn,
    )

    private fun inviteExpiredSnackbar() = SnackbarMessage(
        title = UIText.Resource(Res.string.match_invite_expired_title),
        description = UIText.Resource(Res.string.match_invite_expired_description),
        variant = SnackbarVariant.Info,
    )

    private fun matchDisputedSnackbar() = SnackbarMessage(
        title = UIText.Resource(Res.string.match_disputed_title),
        description = UIText.Resource(Res.string.match_disputed_description),
        variant = SnackbarVariant.Warn,
    )

    private fun matchEndedSnackbar() = SnackbarMessage(
        title = UIText.Resource(Res.string.match_ended_title),
        description = UIText.Resource(Res.string.match_ended_description),
        variant = SnackbarVariant.Info,
    )

    private fun inviteFailedSnackbar() = SnackbarMessage(
        title = UIText.Resource(Res.string.match_invite_failed_title),
        description = UIText.Resource(Res.string.match_invite_failed_description),
        variant = SnackbarVariant.Error,
    )

    private fun reconcileFromSnapshot() {
        val epochAtStart = socketEpoch
        viewModelScope.launch {
            repeat(RECONCILE_MAX_ATTEMPTS) { attempt ->
                if (socketEpoch != epochAtStart) return@launch

                when (val result = matchRepository.getMatchSnapshot(eventId)) {
                    is Result.Success -> {
                        if (socketEpoch != epochAtStart) return@launch
                        val snapshot = result.data
                        if (snapshot == null) onMatchEnded() else applySnapshot(snapshot)
                        return@launch
                    }

                    is Result.Failure -> {
                        if (attempt == RECONCILE_MAX_ATTEMPTS - 1) return@launch
                        delay(RECONCILE_RETRY_BASE_DELAY_MS shl attempt)
                    }
                }
            }
        }
    }

    private fun applySnapshot(snapshot: MatchSnapshot) {
        val phase = snapshot.toMatchPhase()
        _state.update {
            it.copy(
                phase = phase,
                currentUserId = it.currentUserId ?: snapshot.me.userId,
                matchId = snapshot.matchId,
                opponentFullName = snapshot.opponent.fullName,
                opponentProfilePictureUrl =
                    it.opponentProfilePictureUrl?.takeIf { _ -> it.matchId == snapshot.matchId },
                amIWinner = snapshot.amIWinnerOrNull(),
                activeTask = snapshot.task ?: it.activeTask,
                incomingInvite = null,
                isRespondingToInvite = false,
                showMatchRequestSheet = false,
                isSendingInvite = false,
                showOutgoingInviteSheet = false,
                outgoingOpponentName = null,
                outgoingOpponentPhotoUrl = null,
                errorMessage = null,
            )
        }

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
        resetToIdle()
        viewModelScope.launch { snackbarController.show(matchEndedSnackbar()) }
    }

    /**
     * Maça özel state'i temizleyip QR/scan home'una (Idle) döner; soket bağlantısı, kimlik ve
     * lobi (maça değil etkinliğe ait) korunur.
     */
    private fun resetToIdle() {
        _state.update {
            MatchSessionState(
                connectionState = it.connectionState,
                lobbyTargetEpochMillis = it.lobbyTargetEpochMillis,
                lobbyMembers = it.lobbyMembers,
                lobbyTotalCount = it.lobbyTotalCount,
                matchQrToken = it.matchQrToken,
                currentUserId = it.currentUserId,
                currentUserPhotoUrl = it.currentUserPhotoUrl,
                currentUserName = it.currentUserName,
                currentUserScore = it.currentUserScore,
                currentUserWinCount = it.currentUserWinCount,
                currentUserMatchesCount = it.currentUserMatchesCount,
            )
        }
    }

    private fun subscribeToEvents() {
        eventConnectionClient.observeEvents(eventId)
            .onEach { message ->
                // Handler hatası tek mesajı feda etsin, aboneliği değil: hata catch'e
                // düşünce akış kalıcı ölüyor ve banner "Connected" gösterirken davet/faz/
                // stats push'ları sessizce kayboluyordu.
                try {
                    onSocketMessage(message)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Throwable) {
                    logger.error("Socket message handling failed: ${message::class.simpleName}", e)
                }
            }
            .catch { error -> _state.update { it.copy(errorMessage = error.message) } }
            .launchIn(viewModelScope)
    }

    private suspend fun onSocketMessage(message: GameSocketMessage) {
        when (message) {
            is GameSocketMessage.Connected,
            is GameSocketMessage.GameStarted,
            is GameSocketMessage.LobbyUserJoined,
            is GameSocketMessage.LobbyUserLeft,
            is GameSocketMessage.Unknown,
            -> Unit

            else -> socketEpoch++
        }
        when (message) {
            is GameSocketMessage.Connected -> {
                val targetEpochMillis = runCatching {
                    Instant.parse(message.gameStartsAt).toEpochMilliseconds()
                }.getOrDefault(0L)
                _state.update {
                    it.copy(
                        lobbyTargetEpochMillis = targetEpochMillis,
                        lobbyMembers = message.members.take(MAX_LOBBY_AVATARS).map { member ->
                            member.toPresentation()
                        },
                        lobbyTotalCount = message.totalCount,
                        currentUserScore = message.me?.score ?: it.currentUserScore,
                        currentUserWinCount = message.me?.winCount ?: it.currentUserWinCount,
                        currentUserMatchesCount = message.me?.matchesCount ?: it.currentUserMatchesCount,
                    )
                }
                scheduleGameEnd(message.gameEndsAt)
            }

            is GameSocketMessage.LobbyUserJoined -> {
                _state.update { state ->
                    val members = state.lobbyMembers.toMutableList()
                    if (message.fullName != null && members.size < MAX_LOBBY_AVATARS) {
                        members.add(message.toPresentation())
                    }
                    state.copy(lobbyMembers = members, lobbyTotalCount = message.totalCount)
                }
            }

            is GameSocketMessage.LobbyUserLeft -> {
                _state.update { state ->
                    val members = state.lobbyMembers.filter { it.id != message.userId }
                    state.copy(lobbyMembers = members, lobbyTotalCount = message.totalCount)
                }
            }

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
                    val opponentPhotoUrl =
                        it.incomingInvite?.fromProfilePictureUrl ?: it.outgoingOpponentPhotoUrl
                    it.copy(
                        showMatchRequestSheet = false,
                        incomingInvite = null,
                        showOutgoingInviteSheet = false,
                        outgoingOpponentName = null,
                        outgoingOpponentPhotoUrl = null,
                        matchId = message.matchId,
                        opponentFullName = message.opponentFullName,
                        opponentProfilePictureUrl = opponentPhotoUrl,
                        phase = RpsReady(),
                    )
                }
            }

            is GameSocketMessage.MatchInviteDeclined -> {
                _state.update {
                    it.copy(
                        showOutgoingInviteSheet = false,
                        outgoingOpponentName = null,
                        outgoingOpponentPhotoUrl = null,
                    )
                }
                snackbarController.show(inviteDeclinedSnackbar())
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

            is GameSocketMessage.MatchDisputed -> {
                _state.update { state ->
                    val phase = state.phase as? WhoWon ?: return@update state
                    state.copy(
                        phase = phase.copy(
                            isReporting = false,
                            myResultClaimWon = null,
                            opponentClaimedWinnerUserId = null,
                        ),
                        amIWinner = null,
                    )
                }
                // Sessiz reset kafa karıştırıyordu: seçimler nedensiz kayboluyordu.
                snackbarController.show(matchDisputedSnackbar())
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
                _state.update { state ->
                    val myId = state.currentUserId
                    val iWon = myId != null && myId == message.winnerUserId
                    val iPlayed = myId != null && (myId == message.winnerUserId || myId == message.loserUserId)
                    state.copy(
                        phase = Scoreboard(completed = message.completed),
                        currentUserScore = when {
                            !iPlayed -> state.currentUserScore
                            iWon -> message.winnerTotalScore
                            else -> message.loserTotalScore
                        },
                        currentUserWinCount = when {
                            !iPlayed -> state.currentUserWinCount
                            iWon -> message.winnerWinCount
                            else -> message.loserWinCount
                        },
                        currentUserMatchesCount = when {
                            !iPlayed -> state.currentUserMatchesCount
                            iWon -> message.winnerMatchesCount
                            else -> message.loserMatchesCount
                        },
                    )
                }
                loadScoreboard()
            }

            is GameSocketMessage.MatchInviteExpired -> {
                val wasOutgoing = _state.value.incomingInvite?.inviteId != message.inviteId
                _state.update { state ->
                    if (state.incomingInvite?.inviteId == message.inviteId) {
                        state.copy(incomingInvite = null, showMatchRequestSheet = false)
                    } else {
                        state.copy(
                            showOutgoingInviteSheet = false,
                            outgoingOpponentName = null,
                            outgoingOpponentPhotoUrl = null,
                        )
                    }
                }
                // Yalnız giden davetin süresi dolunca bilgilendir; gelen davet sessizce kaybolur.
                if (wasOutgoing) snackbarController.show(inviteExpiredSnackbar())
            }

            is GameSocketMessage.MatchCancelled -> {
                // Yalnızca ayrılmayan tarafa gelir: rakip forfeit etti, sunucu beni kazanan sayıp
                // scoreboard'u (winner=5, loser=0) doldurdu. Puan tablosunu "rakip maçtan ayrıldı"
                // (forfeit=true → kazanan tarafı için ayrıldı altyazısı) olarak göster.
                _state.update {
                    it.copy(matchId = message.matchId, phase = Scoreboard(completed = false, forfeit = true))
                }
                loadScoreboard()
            }

            is GameSocketMessage.GameStarted,
            is GameSocketMessage.Unknown -> Unit
        }
    }

    private var gameEndJob: Job? = null

    /**
     * Backend oyun bitişi için push göndermez (ServerMessageType'ta GAME_ENDED yok);
     * bitiş CONNECTED'taki gameEndsAt'e kurulan client zamanlayıcısıyla tespit edilir.
     * Her (yeniden) bağlantıda tazelenir; süre geçmişse anında tetiklenir.
     */
    private fun scheduleGameEnd(gameEndsAt: String) {
        val endEpochMillis = runCatching {
            Instant.parse(gameEndsAt).toEpochMilliseconds()
        }.getOrNull() ?: return
        gameEndJob?.cancel()
        gameEndJob = viewModelScope.launch {
            val remaining = endEpochMillis - Clock.System.now().toEpochMilliseconds()
            delay(remaining.coerceAtLeast(0L))
            eventChannel.send(MatchSessionEvent.NavigateToLeaderboard)
        }
    }

    // endregion

    // region Actions
    fun onAction(action: MatchSessionAction) {
        when (action) {
            MatchSessionAction.OnBackClick -> _state.update { it.copy(showExitConfirmDialog = true) }
            MatchSessionAction.OnExitDismissed -> _state.update { it.copy(showExitConfirmDialog = false) }
            MatchSessionAction.OnExitConfirmed -> onExitConfirmed()
            MatchSessionAction.OnLobbyCountdownFinished ->
                _state.update { it.copy(showExitConfirmDialog = false, showGameStartSheet = true) }

            MatchSessionAction.OnEnterGameClick -> {
                _state.update { it.copy(showGameStartSheet = false) }
                sendEvent(MatchSessionEvent.NavigateToGame)
            }

            MatchSessionAction.OnLobbyExitConfirmed -> {
                _state.update { it.copy(showExitConfirmDialog = false, showGameStartSheet = false) }
                sendEvent(MatchSessionEvent.NavigateBack)
            }
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
                .onSuccess {
                    // Kazanan otoriter HTTP 200'ü (state=Completed) aldı; geçişi kendine gelen
                    // TASK_FINISHED push'una bırakma — push kaçarsa spinner'da sonsuza dek takılırdı.
                    // Soket yine gelirse handler aynı fazı idempotent set eder + app bar stat'larını tazeler.
                    _state.update { state ->
                        if (state.phase !is WinnerConfirms) return@update state
                        state.copy(phase = Scoreboard(completed = completed))
                    }
                    loadScoreboard()
                }
                .onFailure { error ->
                    _state.update { state ->
                        val current = state.phase as? WinnerConfirms ?: return@update state
                        state.copy(
                            phase = current.copy(isConfirming = false),
                            errorMessage = error.toString(),
                        )
                    }
                    snackbarController.show(error.toSnackbarMessage())
                }
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
                    snackbarController.show(error.toSnackbarMessage())
                }
        }
    }

    private fun finishMatch() {
        val matchId = _state.value.matchId ?: return
        val phase = _state.value.phase as? Scoreboard ?: return
        if (phase.isFinishing) return
        // Forfeit: backend cancel() her iki katılımcıyı zaten Available yaptı ve /finish yalnızca
        // Completed maçı kabul eder (Cancelled'da MatchNotCompletedException atar). Lokal sıfırla.
        if (phase.forfeit) {
            resetToIdle()
            return
        }
        _state.update { it.copy(phase = phase.copy(isFinishing = true), errorMessage = null) }

        viewModelScope.launch {
            matchRepository.finishMatch(eventId = eventId, matchId = matchId)
                .onSuccess {
                    // Maç bitti; soket push'u yok. Ekranı QR/scan home'una (Idle) sıfırlar.
                    resetToIdle()
                }
                .onFailure { error ->
                    _state.update { state ->
                        val current = state.phase as? Scoreboard ?: return@update state
                        state.copy(
                            phase = current.copy(isFinishing = false),
                            errorMessage = error.toString(),
                        )
                    }
                    snackbarController.show(error.toSnackbarMessage())
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
                    snackbarController.show(error.toSnackbarMessage())
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
                    snackbarController.show(error.toSnackbarMessage())
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
                    snackbarController.show(error.toSnackbarMessage())
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
                    snackbarController.show(error.toSnackbarMessage())
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
                    snackbarController.show(error.toSnackbarMessage())
                }
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
                    snackbarController.show(error.toSnackbarMessage())
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
                            outgoingOpponentPhotoUrl = invite.toProfilePictureUrl,
                            showOutgoingInviteSheet = true,
                        )
                    }
                }
                .onFailure {
                    // Bayat QR / rakip meşgul / ağ: kullanıcıya bildir, scanner yeniden kurulur.
                    _state.update { it.copy(isSendingInvite = false) }
                    snackbarController.show(inviteFailedSnackbar())
                }
        }
    }

    private fun loadMyParticipant(attempt: Int = 0) {
        // App-bar stats'ının HTTP'den tazelenme sebebi: soketin replay'lediği CONNECTED
        // snapshot'ı bağlantı anına ait — ekrandan çıkıp soket ölmeden (5 sn) dönen VM'e
        // bayat skor gelir. Bu istek DB gerçeğini getirir; epoch guard, yanıt uçuştayken
        // TASK_FINISHED ile artan skorun geri ezilmesini önler.
        val epochAtStart = socketEpoch
        viewModelScope.launch {
            matchRepository.getMyParticipant(eventId)
                .onSuccess { participant ->
                    val statsFresh = socketEpoch == epochAtStart
                    _state.update {
                        it.copy(
                            matchQrToken = participant.matchQrToken,
                            currentUserId = participant.userId,
                            currentUserScore =
                                if (statsFresh) participant.score else it.currentUserScore,
                            currentUserWinCount =
                                if (statsFresh) participant.winCount else it.currentUserWinCount,
                            currentUserMatchesCount =
                                if (statsFresh) participant.matchesCount else it.currentUserMatchesCount,
                        )
                    }
                }
                .onFailure { error ->
                    // QR üretimi ve kazanan tespiti currentUserId'ye bağlı; sessiz bırakma.
                    if (attempt < 4) {
                        delay(2_000L * (attempt + 1))
                        loadMyParticipant(attempt + 1)
                    } else {
                        snackbarController.show(error.toSnackbarMessage())
                    }
                }
        }
    }

    private fun onExitConfirmed() {
        _state.update { it.copy(showExitConfirmDialog = false) }
        val matchId = _state.value.matchId
        val phase = _state.value.phase
        val shouldForfeit =
            matchId != null && phase != MatchPhase.Idle && phase !is MatchPhase.Scoreboard
        if (!shouldForfeit) {
            sendEvent(MatchSessionEvent.NavigateToDashboard)
            return
        }
        // Maçtan ayrılmak = forfeit. Etkinlikten çıkmıyoruz; rakiple aynı sonuç (scoreboard)
        // ekranına düşüp oradan QR home'a dönüyoruz. Backend cancel() ikisini de Available yapar.
        viewModelScope.launch {
            matchRepository.cancelMatch(eventId = eventId, matchId = matchId)
                .onSuccess {
                    _state.update { it.copy(phase = Scoreboard(completed = false, forfeit = true)) }
                    loadScoreboard()
                }
                .onFailure { error ->
                    _state.update { it.copy(errorMessage = error.toString()) }
                    snackbarController.show(error.toSnackbarMessage())
                }
        }
    }

    private fun sendEvent(event: MatchSessionEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }
    // endregion

    private fun com.kaanf.game.domain.model.LobbyMember.toPresentation(): LobbyMember {
        return LobbyMember(
            id = userId,
            avatar = UserAvatar(
                label = fullName.take(1).uppercase(),
                color = avatarPaletteColor(userId),
                imageUrl = profilePictureUrl,
            ),
        )
    }

    private fun GameSocketMessage.LobbyUserJoined.toPresentation(): LobbyMember {
        val name = fullName.orEmpty()
        return LobbyMember(
            id = userId,
            avatar = UserAvatar(
                label = name.take(1).uppercase(),
                color = avatarPaletteColor(userId),
                imageUrl = profilePictureUrl,
            ),
        )
    }

    private companion object {
        const val RESULT_REPORT_DELAY_MS = 100L
        const val RESULT_CONFIRM_REVEAL_DELAY_MS = 900L
        const val RECONCILE_MAX_ATTEMPTS = 3
        const val RECONCILE_RETRY_BASE_DELAY_MS = 1_000L
        const val MAX_LOBBY_AVATARS = 13
    }
}
