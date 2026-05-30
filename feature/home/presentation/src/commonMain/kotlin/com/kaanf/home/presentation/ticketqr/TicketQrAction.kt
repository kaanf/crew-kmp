package com.kaanf.home.presentation.ticketqr

sealed interface TicketQrAction {
    data object OnEventCodeClicked : TicketQrAction
}
