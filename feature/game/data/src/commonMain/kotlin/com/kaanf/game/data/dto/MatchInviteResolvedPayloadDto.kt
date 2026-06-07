package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchInviteResolvedPayloadDto(
    val inviteId: String,
    val eventId: String,
)
