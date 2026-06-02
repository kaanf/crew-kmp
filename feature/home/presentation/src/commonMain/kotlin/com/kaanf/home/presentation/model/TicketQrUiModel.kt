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
    val formattedVenueAddress: String,
    val formattedDoorTime: String,
)
