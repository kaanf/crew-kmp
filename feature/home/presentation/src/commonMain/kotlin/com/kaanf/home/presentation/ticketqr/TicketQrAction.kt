package com.kaanf.home.presentation.ticketqr

sealed interface TicketQrAction {
    // Qr fazından event-code girişine geçer.
    data object OnEventCodeClicked : TicketQrAction

    // Event-code fazında geri (sistem geri / AppTopBar) → Qr fazına döner.
    data object OnBackClick : TicketQrAction

    data class OnCodeChanged(val code: String) : TicketQrAction

    // Event-code fazında girilen kodu temizler.
    data object OnClearCode : TicketQrAction

    // Bilet yüklemesi başarısız olduğunda tekrar dener.
    data object OnRetryLoad : TicketQrAction
}
