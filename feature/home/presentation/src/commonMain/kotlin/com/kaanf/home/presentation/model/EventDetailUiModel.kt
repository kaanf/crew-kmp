package com.kaanf.home.presentation.model

import com.kaanf.core.domain.model.event.EventId
import kotlin.time.Instant

data class EventDetailUiModel(
    val id: EventId,
    val title: String,
    val description: String?,
    val heroDate: String,
    val hasMyTicket: Boolean,
    val doorsOpenAt: Instant,
    val gameTime: String,
    val crew: String,
    val formattedPrice: String,
    val isFree: Boolean,
)
