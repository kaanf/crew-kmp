package com.kaanf.home.presentation.eventcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.home.presentation.eventcode.component.CodeFieldStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventCodeViewModel : ViewModel() {
    private val eventChannel = Channel<EventCodeEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(EventCodeState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EventCodeState(),
        )

    fun onAction(action: EventCodeAction) {
        when (action) {
            is EventCodeAction.OnCodeChanged -> onCodeChanged(action.code)
        }
    }

    private fun onCodeChanged(code: String) {
        val status = if (code == CORRECT_CODE) {
            CodeFieldStatus.Success
        } else {
            CodeFieldStatus.Editing
        }

        _state.update { it.copy(eventCode = code, status = status) }

        if (status == CodeFieldStatus.Success) {
            viewModelScope.launch {
                eventChannel.send(EventCodeEvent.CodeSuccess)
            }
        }
    }

    private companion object {
        const val CORRECT_CODE = "CREW"
    }
}
