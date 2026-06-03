package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameStartedPayloadDto(
    val eventId: String,
    val gameStartsAt: String,
    val serverNow: String,
)
