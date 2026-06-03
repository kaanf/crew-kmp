package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchStartedPayloadDto(
    val matchId: String,
    val eventId: String,
    val opponentParticipantId: String,
    val opponentUserId: String,
    val opponentFullName: String,
)
