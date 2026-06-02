package com.kaanf.core.domain.model.ticket

import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.model.event.Price
import com.kaanf.core.domain.model.user.UserId
import com.kaanf.core.domain.model.venue.Venue
import kotlinx.serialization.Serializable
import kotlin.time.Instant

data class EventTicket(
    val id: TicketId,
    val eventId: EventId,
    val userId: UserId,
    val entryCode: String,
    val price: Price,
    val status: TicketStatus,
    val purchasedAt: Instant,
    val checkedInAt: Instant?,
)

data class EventTicketContext(
    val title: String,
    val venue: Venue,
    val doorsOpenAt: Instant,
)

data class EventTicketResponse(
    val ticket: EventTicket,
    val eventDetail: EventTicketContext,
    val serverNow: Instant,
)
