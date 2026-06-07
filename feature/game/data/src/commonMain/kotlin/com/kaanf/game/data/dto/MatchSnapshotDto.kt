package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/**
 * `GET /api/events/{eventId}/matches/current` yanıtı. `state` sunucu MatchState adı
 * (örn. "TaskOfferPending"). Aktif maç yoksa sunucu 204 döner; bu DTO hiç gelmez.
 */
@Serializable
data class MatchSnapshotDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val me: MatchParticipantRefDto,
    val opponent: MatchParticipantRefDto,
    val isMeReady: Boolean,
    val isOpponentReady: Boolean,
    val myReportedWinnerUserId: String? = null,
    val opponentReportedWinnerUserId: String? = null,
    val winnerUserId: String? = null,
    val task: MatchSnapshotTaskDto? = null,
    val completed: Boolean,
)

@Serializable
data class MatchParticipantRefDto(
    val participantId: String,
    val userId: String,
    val fullName: String,
)

@Serializable
data class MatchSnapshotTaskDto(
    val taskId: String,
    val title: String,
    val points: Int,
    val categories: List<String> = emptyList(),
)
