package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventDetailDto(
    val id: String,
    val title: String,
    val description: String?,
    val venueId: String,
    val price: PriceSerializable,
    val capacity: Int,
    val soldCount: Int,
    val isFeatured: Boolean,
    val hasMyTicket: Boolean,
    val doorsOpenAt: String,
    val startsAt: String,
    val endsAt: String,
    val status: String,
    val phase: String,
    val createdAt: String,
    val updatedAt: String,
    val imageUrls: List<String> = emptyList(),
)
