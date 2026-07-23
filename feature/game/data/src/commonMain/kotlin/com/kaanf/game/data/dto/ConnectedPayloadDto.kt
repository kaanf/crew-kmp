package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConnectedPayloadDto(
    val eventId: String,
    val gameStartsAt: String,
    val gameEndsAt: String,
    // Oyun içi phase sınırları + sunucu saati; client phase geçişlerini bunlarla
    // lokal hesaplar. Eski sunucuya karşı nullable.
    val boldStartsAt: String? = null,
    val finalStartsAt: String? = null,
    val serverNow: String? = null,
    val totalCount: Int,
    val members: List<LobbyMemberDto>,
    val me: ViewerStatsDto? = null,
)
