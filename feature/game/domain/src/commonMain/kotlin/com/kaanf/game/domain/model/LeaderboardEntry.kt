package com.kaanf.game.domain.model

/** Etkinlik geneli sıralama satırı; oyun bitiminde leaderboard ekranında gösterilir. */
data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val fullName: String,
    val profilePictureUrl: String?,
    val score: Int,
)
