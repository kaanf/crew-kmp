package com.kaanf.game.presentation.gameconfirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameConfirmationViewModel : ViewModel() {
    private val eventChannel = Channel<GameConfirmationEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(GameConfirmationState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: GameConfirmationAction) {
        when (action) {
            GameConfirmationAction.OnBackClick -> onBackClick()
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            eventChannel.send(GameConfirmationEvent.NavigateBack)
        }
    }
}
