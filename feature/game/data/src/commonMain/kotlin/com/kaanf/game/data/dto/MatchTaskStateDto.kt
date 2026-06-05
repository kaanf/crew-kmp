package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchTaskStateDto(
    val matchId: String,
    val eventId: String,
    val state: String,
)
