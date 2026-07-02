package com.kaanf.core.domain.model.event

import com.kaanf.core.domain.model.venue.VenueId
import kotlin.time.Instant

data class EventDashboard(
    val id: EventId,
    val title: String,
    val venueId: VenueId,
    val percentage: Int,
    val price: Price,
    val isFeatured: Boolean,
    val doorsAt: Instant,
    val startsAt: Instant,
    val endsAt: Instant,
    val status: EventStatus,
    val hasMyTicket: Boolean,
)
