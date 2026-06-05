package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** MATCH_RESULT_CONFIRMED push'u; ikinci taraf da onayladığında iki kullanıcıya da gider. */
@Serializable
data class MatchResultConfirmedPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val winnerUserId: String,
)
