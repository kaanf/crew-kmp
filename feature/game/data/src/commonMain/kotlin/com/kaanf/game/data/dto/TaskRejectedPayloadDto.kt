package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** TASK_REJECTED push'u; kaybeden görevi reddedince iki tarafa da gider. */
@Serializable
data class TaskRejectedPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val rejectedByUserId: String,
    val rejectPoints: Int = 0,
    val rejectedByTotalScore: Int? = null,
)
