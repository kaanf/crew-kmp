package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConnectedPayloadDto(
    val eventId: String,
    val gameStartsAt: String,
    val gameEndsAt: String,
    val totalCount: Int,
    val members: List<LobbyMemberDto>,
    val me: ViewerStatsDto? = null,
)
