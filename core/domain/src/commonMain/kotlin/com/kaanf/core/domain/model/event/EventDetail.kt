package com.kaanf.core.domain.model.event

import com.kaanf.core.domain.model.venue.Venue
import com.kaanf.core.domain.model.venue.VenueId
import kotlin.time.Instant

data class EventDetail(
    val id: EventId,
    val title: String,
    val description: String?,
    val venueId: VenueId,
    /** Backend mekânı henüz döndürmüyorsa veya koordinat girilmediyse null; harita bölümü gizlenir. */
    val venue: Venue?,
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
    val imageUrls: List<String>,
)
