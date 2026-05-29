package com.kaanf.home.presentation.dashboard

import com.kaanf.home.presentation.model.EventDashboardUiModel

data class DashboardState(
    val events: List<EventDashboardUiModel> = emptyList(),
    val isLoading: Boolean = false,
)
