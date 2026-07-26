package com.kaanf.core.domain.model.venue

data class Venue(
    val id: VenueId,
    val name: String,
    val district: String,
    val address: String,
    val city: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
)
