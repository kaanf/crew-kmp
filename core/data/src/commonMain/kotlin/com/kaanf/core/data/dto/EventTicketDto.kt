package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventTicketDto(
    val id: String,
    val eventId: String,
    val userId: String,
    val entryCode: String,
    val price: PriceSerializable,
    val status: String,
    val purchasedAt: String,
    val checkedInAt: String?,
)

@Serializable
data class EventTicketContextDto(
    val title: String,
    val venue: VenueDto,
    val doorsOpenAt: String,
)

@Serializable
data class EventTicketResponseDto(
    val ticket: EventTicketDto,
    val eventDetail: EventTicketContextDto,
    val serverNow: String,
)
