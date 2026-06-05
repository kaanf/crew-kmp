package com.kaanf.game.data.repository

/**
 * Sunucunun temiz Close frame ile kapattığı, ama **yeniden denenmemesi gereken** durum
 * (örn. auth/check-in problemi = 1008, geçersiz event = 1003). Token/koşul değişmeden
 * tekrar bağlanmak sonsuz döngü olur.
 */
internal class TerminalSocketException(
    val code: Int?,
    override val message: String?,
) : Exception(message)

/**
 * Geçici kapanış (ping timeout = 1001, sunucu hatası = 1011 ...) — backoff ile yeniden bağlan.
 * Ağ/transport kaynaklı ham exception'lar zaten ayrıca retry'lenir; bu tip yalnızca temiz
 * ama geçici kapanışları işaretler.
 */
internal class RetryableSocketException(
    val code: Int?,
    override val message: String?,
) : Exception(message)
