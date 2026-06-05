package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** `GET /api/tasks` yanıt elemanı. `categories` sunucu enum'ları (örn. "ICEBREAKER"). */
@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val points: Int,
    val categories: List<String> = emptyList(),
)
