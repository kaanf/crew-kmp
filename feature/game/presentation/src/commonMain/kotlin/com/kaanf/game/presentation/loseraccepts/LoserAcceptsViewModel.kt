package com.kaanf.game.presentation.loseraccepts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoserAcceptsViewModel : ViewModel() {
    private val eventChannel = Channel<LoserAcceptsEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(LoserAcceptsState())
    val state =
        _state.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: LoserAcceptsAction) {
        when (action) {
            LoserAcceptsAction.OnBackClick -> onBackClick()
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            eventChannel.send(LoserAcceptsEvent.NavigateBack)
        }
    }
}
