package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** CONNECTED payload'undaki `me` bloğu: bağlanan kullanıcının kendi etkinlik istatistikleri. */
@Serializable
data class ViewerStatsDto(
    val score: Int,
    val winCount: Int,
    val matchesCount: Int,
    /** "WIN" / "LOSS", en yeni maç başta. */
    val recentResults: List<String>,
)
