package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchScoreboardDto(
    val matchId: String,
    val eventId: String,
    val entries: List<MatchScoreboardEntryDto>,
)

@Serializable
data class MatchScoreboardEntryDto(
    val participantId: String,
    val userId: String,
    val fullName: String,
    val role: String,
    val points: Int,
)
