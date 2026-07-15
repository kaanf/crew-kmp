package com.kaanf.game.domain.model

/**
 * Aktif kullanıcının bu etkinlikteki katılımcı kaydı. [userId] sonuç onayında
 * (MATCH_RESULT_CONFIRMED) kazananı kendi kimliğiyle karşılaştırmak için kullanılır.
 */
data class MatchParticipant(
    val id: String,
    val userId: String,
    val matchQrToken: String,
    val score: Int,
    val winCount: Int,
    val matchesCount: Int,
)
