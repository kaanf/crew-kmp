package com.kaanf.game.presentation.whowon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WhoWonViewModel : ViewModel() {
    private val eventChannel = Channel<WhoWonEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(WhoWonState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: WhoWonAction) {
        when (action) {
            WhoWonAction.OnBackClick -> onBackClick()
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            eventChannel.send(WhoWonEvent.NavigateBack)
        }
    }
}
