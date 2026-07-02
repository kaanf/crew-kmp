package com.kaanf.home.presentation.model

import com.kaanf.core.domain.model.event.EventId

data class EventDashboardUiModel(
    val id: EventId,
    val title: String,
    val date: String,
    val percentage: Int,
    val formattedPrice: String,
    val isFeatured: Boolean,
    val hasMyTicket: Boolean,
)
