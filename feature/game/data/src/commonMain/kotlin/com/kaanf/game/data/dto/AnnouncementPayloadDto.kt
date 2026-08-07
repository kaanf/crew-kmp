package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnnouncementPayloadDto(
    val eventId: String,
    val title: String,
    val body: String,
    val durationSeconds: Int? = null,
    val cocktail: AnnouncementCocktailDto? = null,
)

@Serializable
data class AnnouncementCocktailDto(
    val name: String,
    val venueName: String,
    val tagline: String? = null,
    val story: String,
    val signature: String? = null,
    val imageUrl: String? = null,
    val nose: String,
    val palate: String,
    val finish: String,
    val servingNote: String? = null,
)
