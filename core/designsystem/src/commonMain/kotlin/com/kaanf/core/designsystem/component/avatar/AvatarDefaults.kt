package com.kaanf.core.designsystem.component.avatar

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

val AvatarPalette: List<Color> = listOf(
    Color(0xFFFF5A7A),
    Color(0xFFC8FF3D),
    Color(0xFF5BE0C5),
    Color(0xFF6FB7FF),
    Color(0xFFFF7A5C),
    Color(0xFFFFB341),
)

fun avatarPaletteColor(seed: String): Color =
    AvatarPalette[abs(seed.hashCode()) % AvatarPalette.size]

fun avatarContentFor(
    imageUrl: String?,
    initialsLabel: String,
    seed: String,
): AvatarContent = if (!imageUrl.isNullOrBlank()) {
    AvatarContent.Image(imageUrl)
} else {
    AvatarContent.Initials(label = initialsLabel, color = avatarPaletteColor(seed))
}
