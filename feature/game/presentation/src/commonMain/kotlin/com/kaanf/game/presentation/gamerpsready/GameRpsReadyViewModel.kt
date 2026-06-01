package com.kaanf.game.presentation.gamerpsready

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameRpsReadyViewModel : ViewModel() {
    private val eventChannel = Channel<GameRpsReadyEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(GameRpsReadyState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: GameRpsReadyAction) {
        when (action) {
            GameRpsReadyAction.OnBackClick -> onBackClick()
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            eventChannel.send(GameRpsReadyEvent.NavigateBack)
        }
    }
}
