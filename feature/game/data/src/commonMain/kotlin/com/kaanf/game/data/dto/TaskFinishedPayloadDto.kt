package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** TASK_FINISHED push'u; kazanan görevi onaylayınca iki tarafa da gider. */
@Serializable
data class TaskFinishedPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val completed: Boolean,
    val winnerUserId: String,
    val loserUserId: String,
    val winnerPointsAwarded: Int,
    val loserPointsAwarded: Int,
    val winnerTotalScore: Int,
    val loserTotalScore: Int,
)
