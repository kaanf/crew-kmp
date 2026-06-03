package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** MATCH_INVITE_DECLINED (ve ileride EXPIRED) için ortak payload. */
@Serializable
data class MatchInviteResolvedPayloadDto(
    val inviteId: String,
    val eventId: String,
)
