package com.kaanf.home.presentation.ticketqr

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.util.UIText
import com.kaanf.home.domain.usecase.CheckInUseCase
import com.kaanf.home.domain.usecase.GetMyTicketUseCase
import com.kaanf.home.presentation.eventcode.component.CodeFieldStatus
import com.kaanf.home.presentation.mapper.toUiModel
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.ticket_qr_checkin_failed_description
import crew.feature.home.presentation.generated.resources.ticket_qr_checkin_failed_title
import crew.feature.home.presentation.generated.resources.ticket_qr_checkin_unavailable_description
import crew.feature.home.presentation.generated.resources.ticket_qr_checkin_unavailable_title
import crew.feature.home.presentation.generated.resources.ticket_qr_no_connection_description
import crew.feature.home.presentation.generated.resources.ticket_qr_no_connection_title
import crew.feature.home.presentation.generated.resources.ticket_qr_server_unreachable_description
import crew.feature.home.presentation.generated.resources.ticket_qr_server_unreachable_title
import crew.feature.home.presentation.generated.resources.ticket_qr_ticket_not_found_description
import crew.feature.home.presentation.generated.resources.ticket_qr_ticket_not_found_title
import crew.feature.home.presentation.generated.resources.ticket_qr_wrong_code_description
import crew.feature.home.presentation.generated.resources.ticket_qr_wrong_code_title
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
    private val snackbarController: SnackbarController,
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
                _state.update {
                    it.copy(
                        phase = TicketPhase.Qr,
                        eventCode = "",
                        codeStatus = CodeFieldStatus.Editing,
                    )
                }

            is TicketQrAction.OnCodeChanged -> onCodeChanged(action.code)

            TicketQrAction.OnClearCode ->
                _state.update { it.copy(eventCode = "", codeStatus = CodeFieldStatus.Editing) }

            TicketQrAction.OnRetryLoad -> loadMyTicket()
        }
    }

    private fun loadMyTicket() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, loadFailed = false) }

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
                _state.update { it.copy(isLoading = false, loadFailed = true) }
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

        when (val result = checkInUseCase(eventId = eventId, entryCode = entryCode)) {
            is Result.Success -> {
                _state.update {
                    it.copy(isCheckingIn = false, codeStatus = CodeFieldStatus.Success)
                }
                eventChannel.send(TicketQrEvent.CheckInSuccess)
            }

            is Result.Failure -> {
                // Yalnız "yanlış kod" (403 → FORBIDDEN) hücreleri kırmızıya boyar; ağ/sunucu
                // hatalarında kod yerinde kalır, kullanıcı tekrar deneyebilir.
                val isWrongCode = result.error == DataError.Remote.FORBIDDEN
                _state.update {
                    it.copy(
                        isCheckingIn = false,
                        codeStatus = if (isWrongCode) {
                            CodeFieldStatus.Error
                        } else {
                            CodeFieldStatus.Editing
                        },
                    )
                }
                snackbarController.show(result.error.toCheckInSnackbarMessage())
            }
        }
    }

    private companion object {
        const val CODE_LENGTH = 4
    }
}

/**
 * Check-in başarısızlıklarını kullanıcıya net mesaja çevirir. Networking katmanı yalnız HTTP
 * status'u taşıdığı için ayrımı [DataError.Remote] üzerinden yaparız:
 * FORBIDDEN (403) = backend'in INVALID_ENTRY_CODE'u (yanlış kod), CONFLICT (409) = check-in
 * kapalı (EVENT_NOT_OPEN_FOR_ENTRY / TICKET_NOT_ACTIVE), NOT_FOUND (404) = bilet yok.
 */
private fun DataError.Remote.toCheckInSnackbarMessage(): SnackbarMessage = when (this) {
    DataError.Remote.FORBIDDEN -> SnackbarMessage(
        title = UIText.Resource(Res.string.ticket_qr_wrong_code_title),
        description = UIText.Resource(Res.string.ticket_qr_wrong_code_description),
        variant = SnackbarVariant.Error,
    )

    DataError.Remote.NO_INTERNET -> SnackbarMessage(
        title = UIText.Resource(Res.string.ticket_qr_no_connection_title),
        description = UIText.Resource(Res.string.ticket_qr_no_connection_description),
        variant = SnackbarVariant.Warn,
    )

    DataError.Remote.REQUEST_TIMEOUT,
    DataError.Remote.SERVER_ERROR,
    DataError.Remote.SERVICE_UNAVAILABLE -> SnackbarMessage(
        title = UIText.Resource(Res.string.ticket_qr_server_unreachable_title),
        description = UIText.Resource(Res.string.ticket_qr_server_unreachable_description),
        variant = SnackbarVariant.Error,
    )

    DataError.Remote.NOT_FOUND -> SnackbarMessage(
        title = UIText.Resource(Res.string.ticket_qr_ticket_not_found_title),
        description = UIText.Resource(Res.string.ticket_qr_ticket_not_found_description),
        variant = SnackbarVariant.Error,
    )

    DataError.Remote.CONFLICT -> SnackbarMessage(
        title = UIText.Resource(Res.string.ticket_qr_checkin_unavailable_title),
        description = UIText.Resource(Res.string.ticket_qr_checkin_unavailable_description),
        variant = SnackbarVariant.Warn,
    )

    else -> SnackbarMessage(
        title = UIText.Resource(Res.string.ticket_qr_checkin_failed_title),
        description = UIText.Resource(Res.string.ticket_qr_checkin_failed_description),
        variant = SnackbarVariant.Error,
    )
}
