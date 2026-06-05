package com.kaanf.game.domain.model

/**
 * Tamamlanmış bir maçın puan tablosu. Puanlar maç skorlandığında alınan anlık görüntüden
 * okunur; yani koşan etkinlik toplamı değil, bu maçta kazanılan puanlardır.
 */
data class MatchScoreboard(
    val matchId: String,
    val eventId: String,
    val entries: List<MatchScoreboardEntry>,
)

data class MatchScoreboardEntry(
    val participantId: String,
    val userId: String,
    val fullName: String,
    val isWinner: Boolean,
    val points: Int,
)
