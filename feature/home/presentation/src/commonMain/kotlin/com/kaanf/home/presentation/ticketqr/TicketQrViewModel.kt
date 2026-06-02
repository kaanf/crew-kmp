package com.kaanf.home.presentation.ticketqr

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.util.Result
import com.kaanf.home.domain.usecase.GetMyTicketUseCase
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
}
