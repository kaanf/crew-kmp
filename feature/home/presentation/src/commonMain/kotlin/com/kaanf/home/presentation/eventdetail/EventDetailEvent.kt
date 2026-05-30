package com.kaanf.home.presentation.eventdetail

sealed interface EventDetailEvent {
    data object CheckoutSuccess : EventDetailEvent
}
