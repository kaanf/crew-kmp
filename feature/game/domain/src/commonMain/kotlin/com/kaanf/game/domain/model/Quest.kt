package com.kaanf.game.domain.model

/**
 * Etkinlik questi. Katalog sunucuda sabittir; ilerleme maç geçmişinden türetilir.
 * [completed] hedef dolduğunda true olur; puan ancak claim edilince yazılır.
 *
 * Foto questlerinde ([isPhoto]) ilerleme maç geçmişinden değil gönderilmiş fotoğraftan
 * gelir: fotoğrafı yükleyen de içinde etiketlenenler de görevi tamamlamış sayılır.
 */
data class Quest(
    val key: String,
    val title: String,
    val description: String,
    val points: Int,
    val target: Int,
    /** Foto questlerinde fotoğrafta etiketlenmesi gereken kişi sayısı; diğerlerinde 0. */
    val requiredTags: Int,
    val progress: Int,
    val completed: Boolean,
    val claimed: Boolean,
) {
    val isPhoto: Boolean get() = requiredTags > 0
}
