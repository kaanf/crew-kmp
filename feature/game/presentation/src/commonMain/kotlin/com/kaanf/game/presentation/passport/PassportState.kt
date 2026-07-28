package com.kaanf.game.presentation.passport

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class PassportState(
    val isLoading: Boolean = true,
    /** Odadaki toplam kişi sayısı (ben hariç) = pasaporttaki slot sayısı. */
    val totalSlots: Int = 0,
    val stamps: List<PassportStampUi> = emptyList(),
    /** Host'la maç yapılıp 👑 damgası alındı mı (nadir damga satırı için). */
    val hostStampCollected: Boolean = false,
) {
    val rareCount: Int = stamps.count { it.isRare }
    val emptySlotCount: Int = (totalSlots - stamps.size).coerceAtLeast(0)
}

data class PassportStampUi(
    val id: String,
    val ownerName: String,
    val initial: String,
    val ink: Color,
    val shape: StampShape,
    val rotationDegrees: Float,
    /** Tanışma saati, "20:41" biçiminde. */
    val collectedAt: String,
    val firstMatchWon: Boolean,
    val firstMatchTaskTitle: String?,
    val isRare: Boolean = false,
)

enum class StampShape { Round, Square, Notch, Diamond }
