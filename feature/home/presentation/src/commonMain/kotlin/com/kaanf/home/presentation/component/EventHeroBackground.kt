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

fun Modifier.eventHeroBackground(
    shape: Shape = RoundedCornerShape(20.dp)
): Modifier {
    return this
        .clip(shape)
        .drawWithCache {
            val baseGradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF0D0A08),
                    Color(0xFF251710),
                    Color(0xFF15110D),
                ),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            )

            val orangeGlow = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF8A3B21).copy(alpha = 0.45f),
                    Color(0xFF8A3B21).copy(alpha = 0f),
                ),
                center = Offset(size.width * 0.9f, size.height * 0.1f),
                radius = size.width * 0.7f
            )

            val greenGlow = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4D6415).copy(alpha = 0.45f),
                    Color(0xFF4D6415).copy(alpha = 0f),
                ),
                center = Offset(size.width * 0.1f, size.height * 1.05f),
                radius = size.width * 0.75f
            )

            val stripeColor = Color.White.copy(alpha = 0.035f)
            val stripeStep = 18.dp.toPx()
            val stripeStroke = 2.dp.toPx()

            onDrawBehind {
                drawRect(baseGradient)
                drawRect(orangeGlow)
                drawRect(greenGlow)

                var x = -size.height

                while (x < size.width + size.height) {
                    drawLine(
                        color = stripeColor,
                        start = Offset(x, 0f),
                        end = Offset(x + size.height, size.height),
                        strokeWidth = stripeStroke
                    )
                    x += stripeStep
                }
            }
        }
        .border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.08f),
            shape = shape
        )
}

