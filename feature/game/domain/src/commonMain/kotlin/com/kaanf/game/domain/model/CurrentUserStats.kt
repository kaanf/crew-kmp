package com.kaanf.game.domain.model

/**
 * Aktif kullanıcının bu etkinlikteki kendi istatistikleri; QR home app bar'ını besler.
 * CONNECTED snapshot'ında gelir, bu yüzden her (yeniden) bağlanışta tazelenir.
 */
data class CurrentUserStats(
    val score: Int,
    val winCount: Int,
    val matchesCount: Int,
)
