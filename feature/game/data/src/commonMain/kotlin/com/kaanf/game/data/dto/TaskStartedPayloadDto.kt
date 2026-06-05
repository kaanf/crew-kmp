package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** TASK_STARTED push'u; kaybeden görevi kabul edince iki tarafa da gider. */
@Serializable
data class TaskStartedPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val taskId: String,
)
