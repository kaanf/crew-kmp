package com.kaanf.home.presentation.model

import com.kaanf.core.domain.model.event.EventId
import kotlin.time.Instant

data class EventDetailUiModel(
    val id: EventId,
    val title: String,
    val description: String?,
    val heroDate: String,
    val hasMyTicket: Boolean,
    val doorsOpenAt: Instant,
    val goingCount: Int,
    val spotsLeft: Int,
    val formattedPrice: String,
    val isFree: Boolean,
    /** Sunucunun yükleme anındaki fazı: Gameplay → başladı, Finished → bitti; CTA kilitlenir. */
    val isEnded: Boolean,
    /** Sıralı hero görselleri; backend boş dönerse mapper tek elemanlı fallback listesi verir. */
    val imageUrls: List<String>,
    /** Mekânın koordinatı yoksa null; harita bölümü tamamen gizlenir. */
    val location: EventLocationUiModel?,
)
