package com.kaanf.home.presentation.dashboard

import com.kaanf.core.domain.model.event.EventId

sealed interface DashboardEvent {
    data class NavigateToEventDetail(val eventId: EventId) : DashboardEvent
}
