package com.kaanf.game.domain.model

import kotlin.time.Instant

/**
 * Adres defteri (damga pasaportunun veri kaynağı): etkinlikteki herkes bir slot,
 * yalnız tanışılanlar [entries]'te gelir. Kilitli kişiler sunucudan hiç dönmez;
 * istemci [totalCount] - entries.size kadar boş slot çizer.
 */
data class AddressBook(
    val totalCount: Int,
    val entries: List<AddressBookEntry>,
)

data class AddressBookEntry(
    val userId: String,
    val fullName: String,
    val profilePictureUrl: String?,
    /** İlk maçın bitiş anı = tanışma anı. */
    val metAt: Instant,
    /** İlk maçı ben mi kazandım (forfeit dahil). */
    val firstMatchWon: Boolean,
    /** İlk maçta oynanan görev; görevsiz bittiyse (örn. forfeit) null. */
    val firstMatchTaskTitle: String?,
    /** Sunucu lakabı (şimdilik yalnız host 👑). Null = sıradan katılımcı. */
    val title: AddressBookTitle?,
)

data class AddressBookTitle(
    val key: String,
    val label: String,
    val emoji: String,
)
