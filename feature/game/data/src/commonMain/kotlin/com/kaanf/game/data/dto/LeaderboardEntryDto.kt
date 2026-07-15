package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntryDto(
    val rank: Int,
    val userId: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val score: Int,
)
