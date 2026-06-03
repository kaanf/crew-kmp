package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchInviteReceivedPayloadDto(
    val inviteId: String,
    val eventId: String,
    val fromParticipantId: String,
    val fromUserId: String,
    val fromFullName: String,
    val expiresAt: String,
)
