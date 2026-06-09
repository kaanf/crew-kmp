package com.kaanf.game.domain.model

data class LobbyMember(
    val userId: String,
    val fullName: String,
    val profilePictureUrl: String? = null
)
