package com.kaanf.game.domain.model

/**
 * Etkinliğin katılımcı listesindeki bir kişi. Foto questinde etiketlenecek kişiler
 * buradan seçilir; sunucu yalnız check-in'li katılımcıların etiketlenmesine izin verir.
 */
data class EventParticipant(
    val id: String,
    val userId: String,
    val fullName: String,
    val profilePictureUrl: String?,
    val isCheckedIn: Boolean,
)
