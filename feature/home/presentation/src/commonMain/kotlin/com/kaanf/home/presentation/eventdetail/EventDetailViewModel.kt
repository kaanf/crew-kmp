package com.kaanf.home.presentation.eventdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.util.UIText
import com.kaanf.home.domain.usecase.CreateTicketUseCase
import com.kaanf.home.domain.usecase.GetEventDetailUseCase
import com.kaanf.home.presentation.mapper.toUiModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val getEventDetailUseCase: GetEventDetailUseCase,
    private val createTicketUseCase: CreateTicketUseCase,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventId =
        savedStateHandle.get<String>("eventId")
            ?: throw IllegalStateException("No eventId passed to event detail screen")

    private val eventChannel = Channel<EventDetailEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(EventDetailState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EventDetailState(),
        )

    init {
        loadEventDetail()
    }

    fun onAction(action: EventDetailAction) {
        when (action) {
            EventDetailAction.OnCheckoutClicked -> checkout()
            EventDetailAction.GoToTicketQr -> navigate()
        }
    }

    private fun navigate() = viewModelScope.launch {
        val event = _state.value.event ?: return@launch

        eventChannel.send(
            EventDetailEvent.CheckoutSuccess(
                eventId = event.id,
            ),
        )
    }

    private fun checkout() = viewModelScope.launch {
        if (_state.value.isCheckingOut) return@launch

        _state.update { it.copy(isCheckingOut = true) }

        when (createTicketUseCase(eventId)) {
            is Result.Success -> {
                _state.update { it.copy(isCheckingOut = false) }
                navigate()
            }

            is Result.Failure -> {
                _state.update { it.copy(isCheckingOut = false) }
            }
        }
    }

    private fun loadEventDetail() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        when (val result = getEventDetailUseCase(eventId)) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        event = result.data.toUiModel(),
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
