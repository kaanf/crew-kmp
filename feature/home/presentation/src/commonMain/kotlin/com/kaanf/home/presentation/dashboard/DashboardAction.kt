package com.kaanf.home.presentation.dashboard

import com.kaanf.core.domain.model.event.EventId

sealed interface DashboardAction {
    data class OnEventClicked(val id: EventId) : DashboardAction
    data object OnRefresh : DashboardAction
    data object OnResume : DashboardAction
}
