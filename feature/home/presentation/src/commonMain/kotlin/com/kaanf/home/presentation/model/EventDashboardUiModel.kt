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
    val imageUrl: String? = null,
    val dayOfMonth: String = "",
    val monthShort: String = "",
    val doorsTime: String = "",
    val timing: EventTiming = EventTiming.BeforeDoors,
)

enum class EventTiming {
    BeforeDoors,
    DoorsOpen,
    InGame,
}
