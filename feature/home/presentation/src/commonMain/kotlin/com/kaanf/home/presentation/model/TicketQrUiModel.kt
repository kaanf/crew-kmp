package com.kaanf.home.presentation.model

import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.model.ticket.TicketId

data class TicketQrUiModel(
    val id: TicketId,
    val eventId: EventId,
    val entryCode: String,
    val status: String,
    val eventTitle: String,
    val doorsOpenAt: Long,
    val serverClockOffsetMillis: Long,
    val formattedVenueAddress: String,
    val formattedDoorTime: String,
    val formattedDoorClock: String,
    /** Yalnız mekânın koordinatı varsa dolu; harita şeridi bu null değilse çizilir. */
    val location: EventLocationUiModel?,
)
