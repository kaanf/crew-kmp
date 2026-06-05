package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** TASK_REJECTED push'u; kaybeden görevi reddedince yalnızca kazanana gider. */
@Serializable
data class TaskRejectedPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val rejectedByUserId: String,
)
