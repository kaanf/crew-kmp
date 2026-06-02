package com.kaanf.home.presentation.eventcode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.util.Result
import com.kaanf.home.domain.usecase.CheckInUseCase
import com.kaanf.home.presentation.eventcode.component.CodeFieldStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventCodeViewModel(
    private val checkInUseCase: CheckInUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventChannel = Channel<EventCodeEvent>()
    val events = eventChannel.receiveAsFlow()

    private val eventId = savedStateHandle.get<String>("eventId") ?: ""

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
        if (_state.value.status == CodeFieldStatus.Success) return

        _state.update { it.copy(eventCode = code, status = CodeFieldStatus.Editing) }

        if (code.length == CODE_LENGTH) {
            checkIn(code)
        }
    }

    private fun checkIn(entryCode: String) = viewModelScope.launch {
        if (_state.value.isLoading) return@launch

        _state.update { it.copy(isLoading = true) }

        when (checkInUseCase(eventId = eventId, entryCode = entryCode)) {
            is Result.Success -> {
                _state.update {
                    it.copy(isLoading = false, status = CodeFieldStatus.Success)
                }
                eventChannel.send(EventCodeEvent.CodeSuccess)
            }

            is Result.Failure -> {
                _state.update {
                    it.copy(isLoading = false, status = CodeFieldStatus.Error)
                }
            }
        }
    }

    private companion object {
        const val CODE_LENGTH = 4
    }
}
