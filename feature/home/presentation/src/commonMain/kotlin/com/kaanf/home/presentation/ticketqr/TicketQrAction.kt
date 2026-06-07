package com.kaanf.home.presentation.ticketqr

sealed interface TicketQrAction {
    // Qr fazından event-code girişine geçer.
    data object OnEventCodeClicked : TicketQrAction

    // Event-code fazında geri (sistem geri / AppTopBar) → Qr fazına döner.
    data object OnBackClick : TicketQrAction

    data class OnCodeChanged(val code: String) : TicketQrAction
}
