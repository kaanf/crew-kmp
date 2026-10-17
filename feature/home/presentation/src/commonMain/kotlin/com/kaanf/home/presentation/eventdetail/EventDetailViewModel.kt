package com.kaanf.home.presentation.eventdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.util.UIText
import com.kaanf.home.domain.usecase.CreateTicketUseCase
import com.kaanf.home.domain.usecase.GetEventDetailUseCase
import com.kaanf.home.domain.usecase.GetEventGuestsUseCase
import com.kaanf.home.presentation.mapper.toUiModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val getEventDetailUseCase: GetEventDetailUseCase,
    private val getEventGuestsUseCase: GetEventGuestsUseCase,
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
            EventDetailAction.OnRetryLoad -> loadEventDetail()
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
        val currentState = _state.value
        val event = currentState.event ?: return@launch
        if (currentState.isCheckingOut || event.isLocked || event.isEnded || event.hasMyTicket) return@launch

        _state.update { it.copy(isCheckingOut = true) }

        // Keep the loader visible a beat so the button's shrink/grow animation reads well.
        val minLoading = async { delay(MIN_CHECKOUT_LOADING_MS) }
        val result = createTicketUseCase(eventId)
        minLoading.await()

        when (result) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        isCheckingOut = false,
                        event = it.event?.copy(hasMyTicket = true),
                        isGuestsLoading = true,
                    )
                }
                loadGuests()
            }

            is Result.Failure -> {
                _state.update { it.copy(isCheckingOut = false) }
                snackbarController.show(result.error.toSnackbarMessage())
            }
        }
    }

    private fun loadEventDetail() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, loadFailed = false) }

        when (val result = getEventDetailUseCase(eventId)) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        event = result.data.toUiModel(),
                        isGuestsLoading = result.data.hasMyTicket,
                        isLoading = false,
                    )
                }
                if (result.data.hasMyTicket) loadGuests()
            }

            is Result.Failure -> {
                _state.update { it.copy(isLoading = false, loadFailed = true) }
            }
        }
    }

    // Guest list is available only to ticket holders.
    private fun loadGuests() = viewModelScope.launch {
        _state.update { it.copy(isGuestsLoading = true) }
        val result = getEventGuestsUseCase(eventId)
        _state.update {
            it.copy(
                guests = if (result is Result.Success) result.data else it.guests,
                isGuestsLoading = false,
            )
        }
    }

    private companion object {
        const val MIN_CHECKOUT_LOADING_MS = 1_000L
    }
}
