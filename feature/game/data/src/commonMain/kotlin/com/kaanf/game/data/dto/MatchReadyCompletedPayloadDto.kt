package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** MATCH_READY_COMPLETED push'u; iki taraf da "hazır" dediğinde gelir. */
@Serializable
data class MatchReadyCompletedPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
)
