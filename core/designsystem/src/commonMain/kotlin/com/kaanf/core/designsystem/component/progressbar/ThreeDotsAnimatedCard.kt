package com.kaanf.core.designsystem.component.progressbar

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun ThreeDotsAnimatedCard(
    modifier: Modifier = Modifier,
    dotCount: Int = 3,
    dotRadius: Dp = 4.dp,
    spacing: Dp = 16.dp,
    idleColor: Color = AccessDefaults.SurfaceElevated,
    activeColor: Color = AccessDefaults.Accent,
    cycleMillis: Int = 1400,
    maxGlowAlpha: Float = 0.2f,
) {
    val transition = rememberInfiniteTransition(label = "connecting-dots")

    val progresses = (0 until dotCount).map { index ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = cycleMillis / 2,
                    easing = FastOutSlowInEasing,
                ),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset((cycleMillis / 2 / dotCount) * index),
            ),
            label = "dot-$index",
        )
    }

    Canvas(
        modifier = modifier.size(
            width = dotRadius * 2 * dotCount + spacing * (dotCount - 1) + dotRadius * 4,
            height = dotRadius * 6,
        ),
    ) {
        val r = dotRadius.toPx()
        val step = r * 2 + spacing.toPx()
        val totalWidth = step * (dotCount - 1)
        val startX = (size.width - totalWidth) / 2f
        val cy = size.height / 2f

        progresses.forEachIndexed { i, state ->
            val progress = state.value
            val cx = startX + step * i

            val glowAlpha = progress * maxGlowAlpha
            if (glowAlpha > 0.01f) {
                val glowRadius = r * (2f + progress * 1.5f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            activeColor.copy(alpha = glowAlpha),
                            Color.Transparent,
                        ),
                        center = Offset(cx, cy),
                        radius = glowRadius,
                    ),
                    radius = glowRadius,
                    center = Offset(cx, cy),
                )
            }

            drawCircle(
                color = lerp(idleColor, activeColor, progress),
                radius = r,
                center = Offset(cx, cy),
            )
        }
    }
}
