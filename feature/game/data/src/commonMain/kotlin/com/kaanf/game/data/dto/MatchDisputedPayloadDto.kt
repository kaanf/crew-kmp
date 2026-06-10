package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** MATCH_DISPUTED push'u; çelişen sonuç bildiriminde iki tarafa da gider. */
@Serializable
data class MatchDisputedPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val disputedByUserId: String,
)
