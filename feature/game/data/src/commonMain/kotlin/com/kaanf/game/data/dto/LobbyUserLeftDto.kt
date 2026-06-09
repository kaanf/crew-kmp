package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LobbyUserLeftDto(
    val userId: String,
    val totalCount: Int,
)
