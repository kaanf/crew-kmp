package com.kaanf.game.domain.model

/**
 * Etkinlik questi. Katalog sunucuda sabittir; ilerleme maç geçmişinden türetilir.
 * [completed] hedef dolduğunda true olur; puan ancak claim edilince yazılır.
 */
data class Quest(
    val key: String,
    val title: String,
    val description: String,
    val points: Int,
    val target: Int,
    val progress: Int,
    val completed: Boolean,
    val claimed: Boolean,
)
