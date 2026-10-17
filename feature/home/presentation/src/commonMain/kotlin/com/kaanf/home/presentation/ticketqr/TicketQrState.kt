package com.kaanf.home.presentation.ticketqr

import com.kaanf.home.presentation.eventcode.component.CodeFieldStatus
import com.kaanf.home.presentation.model.EventDetailUiModel
import com.kaanf.home.presentation.model.TicketQrUiModel

data class TicketQrState(
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    val ticket: TicketQrUiModel? = null,
    val eventDetail: EventDetailUiModel? = null,
    val phase: TicketPhase = TicketPhase.Qr,
    val eventCode: String = "",
    val codeStatus: CodeFieldStatus = CodeFieldStatus.Editing,
    val isCheckingIn: Boolean = false,
)
