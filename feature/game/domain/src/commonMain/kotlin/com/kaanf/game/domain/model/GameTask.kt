package com.kaanf.game.domain.model

data class GameTask(
    val id: String,
    val title: String,
    val points: Int,
    // Reddedilince skora uygulanan işaretli, göreve özel ceza (örn. -5, -35).
    val rejectPoints: Int = -5,
    val category: TaskCategory,
)
