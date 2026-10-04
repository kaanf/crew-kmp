package com.kaanf.game.domain.model

import kotlin.time.Instant

/**
 * Etkinlik fotoğrafı: PHOTO görevinde kaybedenin çektiği kare (eskiden foto questleri).
 * Oyun sürerken liste yalnız kullanıcının dahil olduklarını içerir; etkinlik bitince
 * tüm odanınki döner.
 */
data class EventMemory(
    val id: String,
    /** İmzalı, kısa ömürlü URL (backend ~1 saat). Bayatlarsa liste yenilenmeli. */
    val imageUrl: String,
    val ownerName: String,
    val ownerProfilePictureUrl: String?,
    val isMine: Boolean,
    /** Kaldırılmış foto questlerinden kalan fotoğraflarda dolu; yenilerde null. */
    val questKey: String?,
    /** Fotoğrafın çekildiği görevin metni; görevsiz (eski) fotoğraflarda null. */
    val taskTitle: String?,
    val tagged: List<MemoryTag>,
    val capturedAt: Instant,
)

/**
 * Fotoğrafta etiketlenen kişi ve pininin yeri. [pinX]/[pinY] piksel değil, sol üstten
 * itibaren 0-1 oranıdır: sunucu fotoğrafı küçültüp döndürdüğü için piksel kayardı.
 */
data class MemoryTag(
    val participantId: String,
    val fullName: String,
    val profilePictureUrl: String?,
    val pinX: Float,
    val pinY: Float,
)
