package com.kaanf.home.presentation.eventdetail

import com.kaanf.core.domain.model.event.EventId

sealed interface EventDetailEvent {
    data class CheckoutSuccess(val eventId: EventId) : EventDetailEvent
}
