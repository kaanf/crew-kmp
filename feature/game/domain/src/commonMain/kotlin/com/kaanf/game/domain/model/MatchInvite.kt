package com.kaanf.game.domain.model

data class MatchInvite(
    val inviteId: String,
    val eventId: String,
    val toParticipantId: String,
    val toFullName: String,
    val toProfilePictureUrl: String?,
    val expiresAt: String,
)
