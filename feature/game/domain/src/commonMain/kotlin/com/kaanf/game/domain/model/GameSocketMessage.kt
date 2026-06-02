package com.kaanf.game.domain.model

/**
 * Sunucudan WebSocket üzerinden gelen ham event.
 * [type] sunucunun gönderdiği event tipi, [raw] ise tam JSON gövdesi.
 * Sunucu şeması netleşince data katmanında tipli event'lere map edilebilir.
 */
data class GameSocketMessage(
    val type: String,
    val raw: String,
)
