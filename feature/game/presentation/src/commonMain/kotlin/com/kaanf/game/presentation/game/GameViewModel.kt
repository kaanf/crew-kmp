package com.kaanf.game.presentation.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.asEmptyResult
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.game.domain.model.GameSocketMessage
import com.kaanf.game.domain.repository.GameSocketRepository
import com.kaanf.game.domain.repository.MatchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameSocketRepository: GameSocketRepository,
    private val matchRepository: MatchRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId = savedStateHandle.get<String>("eventId").orEmpty()

    private val eventChannel = Channel<GameEvent>()
    val events = eventChannel.receiveAsFlow()

    private var socketJob: Job? = null

    private val _state = MutableStateFlow(GameState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        loadMatchQrToken()
        subscribeToEvents()
    }

    fun onAction(action: GameAction) {
        when (action) {
            GameAction.OnBackClick -> _state.update { it.copy(showExitConfirmDialog = true) }
            GameAction.OnExitDismissed -> _state.update { it.copy(showExitConfirmDialog = false) }
            GameAction.OnExitConfirmed -> onExitConfirmed()
            GameAction.OnScanClicked -> onScanClicked()
            GameAction.OnInviteAccepted -> respondToInvite(navigateToRps = true) { inviteId ->
                matchRepository.acceptInvite(eventId = eventId, inviteId = inviteId).asEmptyResult()
            }
            GameAction.OnInviteDeclined -> respondToInvite(navigateToRps = false) { inviteId ->
                matchRepository.declineInvite(eventId = eventId, inviteId = inviteId)
            }
        }
    }

    private fun respondToInvite(
        navigateToRps: Boolean,
        call: suspend (inviteId: String) -> EmptyResult<DataError.Remote>,
    ) {
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
                    if (navigateToRps) {
                        eventChannel.send(GameEvent.NavigateToGameRpsReady)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isRespondingToInvite = false, errorMessage = error.toString())
                    }
                }
        }
    }

    private fun loadMatchQrToken() {
        viewModelScope.launch {
            matchRepository.getMyMatchQrToken(eventId)
                .onSuccess { token ->
                    _state.update { it.copy(matchQrToken = token) }
                }
        }
    }

    private fun subscribeToEvents() {
        if (socketJob?.isActive == true) return

        socketJob = gameSocketRepository.observeEvents(eventId)
            .onEach(::onSocketMessage)
            .catch { error ->
                _state.update { it.copy(errorMessage = error.message) }
            }
            .launchIn(viewModelScope)
    }

    private fun onSocketMessage(message: GameSocketMessage) {
        when (message) {
            is GameSocketMessage.GameStarted ->
                _state.update { it.copy(gameStarted = true) }

            is GameSocketMessage.MatchInviteReceived ->
                _state.update {
                    it.copy(incomingInvite = message, showMatchRequestSheet = true)
                }

            is GameSocketMessage.MatchStarted,
            is GameSocketMessage.MatchInviteDeclined,
            GameSocketMessage.Connected,
            is GameSocketMessage.Unknown -> Unit
        }
    }

    private fun unsubscribeFromEvents() {
        socketJob?.cancel()
        socketJob = null
    }

    private fun onExitConfirmed() {
        _state.update { it.copy(showExitConfirmDialog = false) }
        unsubscribeFromEvents()
        viewModelScope.launch {
            eventChannel.send(GameEvent.NavigateToDashboard)
        }
    }

    private fun onScanClicked() {
        viewModelScope.launch {
            eventChannel.send(GameEvent.NavigateToScanOpponent)
        }
    }

    override fun onCleared() {
        unsubscribeFromEvents()
        super.onCleared()
    }
}
