package com.kaanf.home.presentation.eventdetail

sealed interface EventDetailAction {
    data object OnCheckoutClicked : EventDetailAction
    data object GoToTicketQr : EventDetailAction
}
