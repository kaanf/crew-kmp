package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults

private val BackdropDot = Color(0x297F7F7F)

/** Board backdrop: the app surface fading into the screen background, under the dot grid. */
@Composable
internal fun BoardBackdrop(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.drawWithCache {
            val brush = Brush.verticalGradient(listOf(AccessDefaults.Surface, AccessDefaults.Background))
            val spacing = 20.dp.toPx()
            val radius = 1.2.dp.toPx()
            onDrawBehind {
                drawRect(brush)
                var y = spacing / 2
                while (y < size.height) {
                    var x = spacing / 2
                    while (x < size.width) {
                        drawCircle(BackdropDot, radius, Offset(x, y))
                        x += spacing
                    }
                    y += spacing
                }
            }
        },
    )
}

/** Dashed rounded outline; [alpha] is read while drawing so animating it skips recomposition. */
internal fun Modifier.dashedBorder(
    color: Color,
    cornerRadius: Dp,
    width: Dp = 2.5.dp,
    alpha: () -> Float = { 1f },
): Modifier = drawWithCache {
    val strokeWidth = width.toPx()
    val stroke = Stroke(
        width = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 6.dp.toPx())),
    )
    val inset = strokeWidth / 2
    val radius = CornerRadius(cornerRadius.toPx() - inset)
    onDrawBehind {
        val currentAlpha = alpha()
        if (currentAlpha > 0f) drawRoundRect(
            color = color,
            topLeft = Offset(inset, inset),
            size = Size(size.width - strokeWidth, size.height - strokeWidth),
            cornerRadius = radius,
            style = stroke,
            alpha = currentAlpha,
        )
    }
}
