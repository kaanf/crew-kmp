package com.kaanf.game.presentation.scanopponent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScanOpponentViewModel : ViewModel() {
    private val eventChannel = Channel<ScanOpponentEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(ScanOpponentState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    fun onAction(action: ScanOpponentAction) {
        when (action) {
            ScanOpponentAction.OnCloseClicked -> onBackClick()
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            eventChannel.send(ScanOpponentEvent.CloseScreen)
        }
    }
}
