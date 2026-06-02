package com.kaanf.core.domain.model.event

import com.kaanf.core.domain.model.venue.VenueId
import kotlin.time.Instant

data class EventDetail(
    val id: EventId,
    val title: String,
    val description: String?,
    val venueId: VenueId,
    val price: Price,
    val capacity: Int,
    val soldCount: Int,
    val isFeatured: Boolean,
    val hasMyTicket: Boolean,
    val doorsOpenAt: Instant,
    val startsAt: Instant,
    val endsAt: Instant,
    val status: EventStatus,
    val phase: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
