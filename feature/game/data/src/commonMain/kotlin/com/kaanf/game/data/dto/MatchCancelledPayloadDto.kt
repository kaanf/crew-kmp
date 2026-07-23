package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** MATCH_CANCELLED push'u; bir oyuncu maçtan ayrılınca/koptuğunda (forfeit) rakibine gider. */
@Serializable
data class MatchCancelledPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val cancelledByUserId: String,
    val winnerUserId: String? = null,
    val winnerTotalScore: Int? = null,
    val winnerPointsAwarded: Int = 0,
)
