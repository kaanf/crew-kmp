package com.kaanf.game.domain.model

import kotlin.time.Instant

/**
 * Etkinlik içinde çekilmiş bir "memory" fotoğrafı. Oyun sürerken liste yalnız
 * kullanıcının kendi çektiklerini içerir; etkinlik bitince tüm odanın fotoğrafları döner.
 */
data class EventMemory(
    val id: String,
    /** İmzalı, kısa ömürlü URL (backend ~1 saat). Bayatlarsa liste yenilenmeli. */
    val imageUrl: String,
    val ownerName: String,
    val ownerProfilePictureUrl: String?,
    val isMine: Boolean,
    val capturedAt: Instant,
)
