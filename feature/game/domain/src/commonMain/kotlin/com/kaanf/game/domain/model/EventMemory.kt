package com.kaanf.game.domain.model

import kotlin.time.Instant

/**
 * Bir foto questine gönderilmiş fotoğraf. Oyun sürerken liste yalnız kullanıcının
 * yüklediklerini ve etiketlendiklerini içerir; etkinlik bitince tüm odanınki döner.
 */
data class EventMemory(
    val id: String,
    /** İmzalı, kısa ömürlü URL (backend ~1 saat). Bayatlarsa liste yenilenmeli. */
    val imageUrl: String,
    val ownerName: String,
    val ownerProfilePictureUrl: String?,
    val isMine: Boolean,
    /** Ait olduğu foto questi; serbest çekim döneminden kalan fotoğraflarda null. */
    val questKey: String?,
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

/** Yüklerken gönderilen etiket: kim, fotoğrafın neresinde. */
data class QuestPhotoTag(
    val participantId: String,
    val pinX: Float,
    val pinY: Float,
)
