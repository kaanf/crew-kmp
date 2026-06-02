package com.kaanf.game.presentation.winnerwaits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WinnerWaitsViewModel : ViewModel() {
    private val eventChannel = Channel<WinnerWaitsEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(WinnerWaitsState())
    val state =
        _state.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: WinnerWaitsAction) {
        when (action) {
            WinnerWaitsAction.OnBackClick -> onBackClick()
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            eventChannel.send(WinnerWaitsEvent.NavigateBack)
        }
    }
}
