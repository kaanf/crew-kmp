package com.kaanf.home.presentation.ticketqr

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.util.Result
import com.kaanf.home.domain.usecase.CheckInUseCase
import com.kaanf.home.domain.usecase.GetMyTicketUseCase
import com.kaanf.home.presentation.eventcode.component.CodeFieldStatus
import com.kaanf.home.presentation.mapper.toUiModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TicketQrViewModel(
    private val getMyTicketUseCase: GetMyTicketUseCase,
    private val checkInUseCase: CheckInUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId =
        savedStateHandle.get<String>("eventId")
            ?: throw IllegalStateException("No eventId passed to ticket qr screen")

    private val eventChannel = Channel<TicketQrEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(TicketQrState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TicketQrState(),
        )

    init {
        loadMyTicket()
    }

    fun onAction(action: TicketQrAction) {
        when (action) {
            TicketQrAction.OnEventCodeClicked ->
                _state.update { it.copy(phase = TicketPhase.EventCode) }

            TicketQrAction.OnBackClick ->
                _state.update { it.copy(phase = TicketPhase.Qr) }

            is TicketQrAction.OnCodeChanged -> onCodeChanged(action.code)
        }
    }

    private fun loadMyTicket() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = getMyTicketUseCase(eventId)) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        ticket = result.data.toUiModel(),
                        isLoading = false,
                    )
                }
            }

            is Result.Failure -> {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun onCodeChanged(code: String) {
        if (_state.value.codeStatus == CodeFieldStatus.Success) return

        _state.update { it.copy(eventCode = code, codeStatus = CodeFieldStatus.Editing) }

        if (code.length == CODE_LENGTH) {
            checkIn(code)
        }
    }

    private fun checkIn(entryCode: String) = viewModelScope.launch {
        if (_state.value.isCheckingIn) return@launch

        _state.update { it.copy(isCheckingIn = true) }

        when (checkInUseCase(eventId = eventId, entryCode = entryCode)) {
            is Result.Success -> {
                _state.update {
                    it.copy(isCheckingIn = false, codeStatus = CodeFieldStatus.Success)
                }
                eventChannel.send(TicketQrEvent.CheckInSuccess)
            }

            is Result.Failure -> {
                _state.update {
                    it.copy(isCheckingIn = false, codeStatus = CodeFieldStatus.Error)
                }
            }
        }
    }

    private companion object {
        const val CODE_LENGTH = 4
    }
}
