package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchInviteDto(
    val inviteId: String,
    val eventId: String,
    val toParticipantId: String,
    val toFullName: String,
    val toProfilePictureUrl: String? = null,
    val expiresAt: String,
)
