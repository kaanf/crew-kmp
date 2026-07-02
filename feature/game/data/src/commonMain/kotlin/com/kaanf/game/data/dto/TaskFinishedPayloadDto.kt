package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** TASK_FINISHED push'u; kazanan görevi onaylayınca iki tarafa da gider. */
@Serializable
data class TaskFinishedPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val completed: Boolean,
    val winnerUserId: String,
    val loserUserId: String,
    val winnerPointsAwarded: Int,
    val loserPointsAwarded: Int,
    val winnerTotalScore: Int,
    val loserTotalScore: Int,
    // ponytail: app bar istatistikleri yalnız zenginleştirme; eksik gelirse (eski/güncellenmemiş
    // backend) faz geçişi TASK_FINISHED'i Unknown'a düşürüp kaybedeni TaskActive'de dondurmasın
    // diye opsiyonel. Default 0; gerçek değerler bir sonraki CONNECTED snapshot'ında tazelenir.
    val winnerWinCount: Int = 0,
    val winnerMatchesCount: Int = 0,
    val loserWinCount: Int = 0,
    val loserMatchesCount: Int = 0,
)
