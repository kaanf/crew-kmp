package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** TASK_OFFERED push'u; kazanan görev seçince yalnızca kaybedene gider. */
@Serializable
data class TaskOfferedPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val offeredByUserId: String,
    val taskId: String,
    val taskTitle: String,
    val taskPoints: Int,
    val taskRejectPoints: Int = -5,
    val taskCategories: List<String> = emptyList(),
)
