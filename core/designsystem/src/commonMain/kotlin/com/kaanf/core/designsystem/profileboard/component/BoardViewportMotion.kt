package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

/** Transform the existing board without remeasuring or recomposing its QR/sticker content per frame. */
internal fun Modifier.animatedBoardViewport(
    progress: State<Float>,
    editorScale: Float,
    editorOffsetY: Dp,
    editorHeightFraction: Float,
): Modifier = graphicsLayer {
    val fraction = progress.value
    val scale = lerp(1f, editorScale, fraction)
    scaleX = scale
    scaleY = scale
    transformOrigin = TransformOrigin(.5f, 0f)
    translationY = editorOffsetY.toPx() * fraction
    clip = fraction > 0f
    shape = BoardViewportClip(lerp(1f, editorHeightFraction, fraction), 22.dp.toPx() * fraction)
}.drawWithCache {
    val strokeWidth = 3.dp.toPx()
    val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.dp.toPx(), 7.dp.toPx())))
    onDrawWithContent {
        drawContent()
        val fraction = progress.value
        if (fraction > 0f) {
            val height = size.height * lerp(1f, editorHeightFraction, fraction)
            drawRoundRect(Color(0xFFF5F4F0).copy(alpha = fraction),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size((size.width - strokeWidth).coerceAtLeast(0f), (height - strokeWidth).coerceAtLeast(0f)),
                cornerRadius = CornerRadius(22.dp.toPx() * fraction), style = stroke)
        }
    }
}

private class BoardViewportClip(private val heightFraction: Float, private val radius: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Rounded(RoundRect(0f, 0f, size.width, size.height * heightFraction, CornerRadius(radius)))
}
