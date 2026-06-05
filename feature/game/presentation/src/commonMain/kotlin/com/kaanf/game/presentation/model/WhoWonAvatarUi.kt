package com.kaanf.game.presentation.model

import androidx.compose.ui.graphics.Color

/** WhoWon satırının yanında, o seçimi yapan oyuncuyu temsil eden avatar. */
data class WhoWonAvatarUi(
    val label: String,
    val color: Color,
    /** Avatar belirince çevresinde tek seferlik vurgu halkası çalsın mı (rakip seçimi için). */
    val highlight: Boolean = false,
)
