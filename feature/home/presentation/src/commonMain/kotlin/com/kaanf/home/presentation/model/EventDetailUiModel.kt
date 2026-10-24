package com.kaanf.home.presentation.model

import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.palette.BackdropPalette

data class EventDetailUiModel(
    val id: EventId,
    val title: String,
    val description: String?,
    val heroDate: String,
    /** "01:00" */
    val doorsTime: String,
    val hasMyTicket: Boolean,
    val formattedPrice: String,
    val isFree: Boolean,
    /** Sunucunun yükleme anındaki fazı: Gameplay → başladı, Finished → bitti; CTA kilitlenir. */
    val isEnded: Boolean,
    val isLocked: Boolean,
    /** Sıralı hero görselleri; backend boş dönerse mapper tek elemanlı fallback listesi verir. */
    val imageUrls: List<String>,
    /** Mekânın koordinatı yoksa null; harita bölümü tamamen gizlenir. */
    val location: EventLocationUiModel?,
    /** Null = no cover colours from the backend; the screen keeps its flat background. */
    val backdropPalette: BackdropPalette? = null,
    val shareUrl: String? = null,
)
