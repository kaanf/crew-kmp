package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/**
 * `GET /api/events/{eventId}/matches/history` satırı. `state` sunucu MatchState adı;
 * bu endpoint'te yalnız "Completed" ya da "Cancelled" gelir.
 */
@Serializable
data class MatchHistoryEntryDto(
    val matchId: String,
    val state: String,
    val won: Boolean,
    val opponentUserId: String? = null,
    val opponentFullName: String = "",
    val opponentAvatarUrl: String? = null,
    val myPoints: Int,
    val opponentPoints: Int,
    val taskTitle: String? = null,
    val startedAt: String,
    val completedAt: String? = null,
)
