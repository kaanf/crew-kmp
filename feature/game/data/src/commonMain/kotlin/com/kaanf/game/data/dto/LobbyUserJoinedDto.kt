package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LobbyUserJoinedDto(
    val userId: String,
    val totalCount: Int,
    val fullName: String? = null,
    val profilePictureUrl: String? = null,
)
