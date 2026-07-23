package com.kaanf.game.domain.model

import kotlin.time.Instant

/**
 * "Your night" tab'ındaki maç geçmişi satırı. Yalnız biten maçlar gelir
 * (Completed veya Cancelled = forfeit); sunucu yeniden eskiye sıralar.
 */
data class MatchHistoryEntry(
    val matchId: String,
    val won: Boolean,
    /** true = maç forfeit ile bitti (biri ayrıldı); won bu durumda "rakip mi ayrıldı"yı söyler. */
    val cancelled: Boolean,
    val opponentUserId: String?,
    val opponentFullName: String,
    val opponentAvatarUrl: String?,
    val myPoints: Int,
    val taskTitle: String?,
    val occurredAt: Instant,
)
