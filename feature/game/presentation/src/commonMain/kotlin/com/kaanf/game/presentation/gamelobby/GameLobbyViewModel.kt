package com.kaanf.game.presentation.gamelobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class GameLobbyViewModel: ViewModel() {
    private val eventChannel = Channel<GameLobbyEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(
        GameLobbyState(
            targetEpochMillis = Clock.System.now().toEpochMilliseconds() + COUNTDOWN_DURATION_MILLIS,
        ),
    )
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: GameLobbyAction) {
        when (action) {
            GameLobbyAction.OnBackClick -> _state.update { it.copy(showExitConfirmDialog = true) }
            GameLobbyAction.OnCountdownFinished -> _state.update { it.copy(showExitConfirmDialog = false, showGameStartSheet = true) }
            GameLobbyAction.OnExitDismissed -> _state.update { it.copy(showExitConfirmDialog = false) }
            GameLobbyAction.OnExitConfirmed -> onExitConfirmed()
            GameLobbyAction.OnEnterGameClick -> onEnterGameClick()
        }
    }

    private fun onEnterGameClick() {
        _state.update { it.copy(showGameStartSheet = false) }
        viewModelScope.launch {
            eventChannel.send(GameLobbyEvent.NavigateToGame)
        }
    }

    private fun onExitConfirmed() {
        _state.update { it.copy(showExitConfirmDialog = false, showGameStartSheet = false) }
        viewModelScope.launch {
            eventChannel.send(GameLobbyEvent.NavigateBack)
        }
    }

    private companion object {
        const val COUNTDOWN_DURATION_MILLIS = 10_000L
    }
}
