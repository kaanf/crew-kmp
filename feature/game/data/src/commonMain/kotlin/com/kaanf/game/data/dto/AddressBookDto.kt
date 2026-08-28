package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/**
 * `GET /api/events/{eventId}/address-book`. Kilitli kişiler cevaba hiç girmez;
 * boş slot sayısı totalCount - entries.size ile bulunur.
 */
@Serializable
data class AddressBookDto(
    val totalCount: Int,
    val entries: List<AddressBookEntryDto> = emptyList(),
)

@Serializable
data class AddressBookEntryDto(
    val participantId: String,
    val userId: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val metAt: String,
    val firstMatchWon: Boolean = false,
    val firstMatchTaskTitle: String? = null,
    val title: TitleDto? = null,
    // Tanışmanın claim değeri ve durumu; default'lar eski sunucuya karşı güvenli
    // (claimed=true → yanlışlıkla claim edilebilir görünmez).
    val points: Int = 25,
    val claimed: Boolean = true,
)

@Serializable
data class TitleDto(
    val key: String,
    val label: String,
    val emoji: String,
)
