package com.kaanf.home.presentation.ticketqr

sealed interface TicketQrEvent {
    data object CheckInSuccess : TicketQrEvent
}
