package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** MATCH_CANCELLED push'u; bir oyuncu maçtan ayrılınca (forfeit) yalnızca rakibine gider. */
@Serializable
data class MatchCancelledPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val cancelledByUserId: String,
)
