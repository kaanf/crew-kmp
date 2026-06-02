package com.kaanf.game.presentation.winnerconfirms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WinnerConfirmsViewModel : ViewModel() {
    private val eventChannel = Channel<WinnerConfirmsEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(WinnerConfirmsState())
    val state =
        _state.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: WinnerConfirmsAction) {
        when (action) {
            WinnerConfirmsAction.OnBackClick -> onBackClick()
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            eventChannel.send(WinnerConfirmsEvent.NavigateBack)
        }
    }
}
