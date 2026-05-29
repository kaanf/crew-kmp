package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventDashboardDto(
    val id: String,
    val title: String,
    val venueId: String,
    val percentage: Int,
    val price: PriceSerializable,
    val isFeatured: Boolean,
    val doorsAt: String,
    val startsAt: String,
    val endsAt: String,
    val status: String,
)
