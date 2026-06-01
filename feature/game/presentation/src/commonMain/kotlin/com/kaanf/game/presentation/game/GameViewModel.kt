package com.kaanf.game.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val eventChannel = Channel<GameEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(GameState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: GameAction) {
        when (action) {
            GameAction.OnBackClick -> _state.update { it.copy(showExitConfirmDialog = true) }
            GameAction.OnExitDismissed -> _state.update { it.copy(showExitConfirmDialog = false) }
            GameAction.OnExitConfirmed -> onExitConfirmed()
            GameAction.OnScanClicked -> onScanClicked()
        }
    }

    private fun onExitConfirmed() {
        _state.update { it.copy(showExitConfirmDialog = false) }
        viewModelScope.launch {
            eventChannel.send(GameEvent.NavigateToDashboard)
        }
    }

    private fun onScanClicked() {
        viewModelScope.launch {
            eventChannel.send(GameEvent.NavigateToScanOpponent)
        }
    }
}
