package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VenueDto(
    val id: String,
    val name: String,
    val district: String,
    val address: String,
    val city: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
)
