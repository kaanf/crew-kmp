package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class LobbyMemberDto(
    val userId: String,
    val fullName: String,
    val profilePictureUrl: String? = null
)
