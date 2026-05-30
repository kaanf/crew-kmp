package com.kaanf.home.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlin.math.min

fun Modifier.eventHeroBackground(
    shape: Shape = RoundedCornerShape(20.dp)
): Modifier {
    return this
        .clip(shape)
        .drawWithCache {
            val minSize = min(size.width, size.height)

            val baseGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    0.00f to Color(0xFF21170F),
                    0.30f to Color(0xFF1A130D),
                    0.55f to Color(0xFF130F0A),
                    0.78f to Color(0xFF0F0B08),
                    1.00f to Color(0xFF0B0805),
                ),
                start = Offset(size.width * 0.15f, 0f),
                end = Offset(size.width * 0.85f, size.height)
            )

            val coralGlow = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to Color(0xFFFF7A5C).copy(alpha = 0.20f),
                    0.24f to Color(0xFFFF7A5C).copy(alpha = 0.13f),
                    0.48f to Color(0xFFFF7A5C).copy(alpha = 0.065f),
                    0.72f to Color(0xFFFF7A5C).copy(alpha = 0.025f),
                    1.00f to Color.Transparent,
                ),
                center = Offset(size.width * 0.98f, size.height * 0.02f),
                radius = minSize * 1.25f
            )

            val limeGlow = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to Color(0xFFC8FF3D).copy(alpha = 0.18f),
                    0.26f to Color(0xFFC8FF3D).copy(alpha = 0.12f),
                    0.50f to Color(0xFFC8FF3D).copy(alpha = 0.06f),
                    0.74f to Color(0xFFC8FF3D).copy(alpha = 0.025f),
                    1.00f to Color.Transparent,
                ),
                center = Offset(size.width * 0.02f, size.height * 1.02f),
                radius = minSize * 1.64f
            )

            val centerDark = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to Color.Black.copy(alpha = 0.18f),
                    0.45f to Color.Black.copy(alpha = 0.10f),
                    0.75f to Color.Black.copy(alpha = 0.04f),
                    1.00f to Color.Transparent,
                ),
                center = Offset(size.width * 0.5f, size.height * 0.52f),
                radius = minSize * 0.55f
            )

            onDrawBehind {
                drawRect(baseGradient)
                drawRect(coralGlow)
                drawRect(limeGlow)
                drawRect(centerDark)
            }
        }
        .border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.12f),
            shape = shape
        )
}
