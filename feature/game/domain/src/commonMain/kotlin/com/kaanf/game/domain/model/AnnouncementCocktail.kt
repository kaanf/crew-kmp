package com.kaanf.game.domain.model

/**
 * Duyuruya iliştirilen kokteyl. Duyuru gönderilirken backend'de kokteylin o anki hâli
 * kopyalanır; chip'e tıklayınca açılan iki sayfalık sheet'i besler
 * (birinci sayfa hikâye, ikinci sayfa tat notları).
 */
data class AnnouncementCocktail(
    val name: String,
    val venueName: String,
    /** Fotoğrafın altındaki küçük etiket, ör. "Never on the menu". */
    val tagline: String?,
    val story: String,
    /** Hikâyenin altındaki imza, ör. "Vít, bar lead". */
    val signature: String?,
    val imageUrl: String?,
    val nose: String,
    val palate: String,
    val finish: String,
    /** Notların altındaki servis cümlesi. */
    val servingNote: String?,
)
