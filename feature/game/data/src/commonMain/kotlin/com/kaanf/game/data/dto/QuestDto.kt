package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuestDto(
    val key: String,
    val title: String,
    val description: String,
    val points: Int,
    val target: Int,
    val progress: Int,
    val completed: Boolean,
    val claimed: Boolean,
)
