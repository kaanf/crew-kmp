package com.kaanf.game.domain.model

data class GameTask(
    val id: String,
    val title: String,
    val points: Int,
    val categories: List<TaskCategory>,
)
