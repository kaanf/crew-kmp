package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.kaanf.core.domain.model.profileboard.BoardStickerPlacement
import kotlin.math.roundToInt

/** Scale the full composition, including gestures, without changing saved coordinates. */
@Composable
internal fun BoardViewport(referenceWidth: Dp, scale: Float, modifier: Modifier = Modifier, contentTop: Float = 0f, height: Float = BoardStickerPlacement.HEIGHT, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { children, _ ->
        val width = referenceWidth.roundToPx().coerceAtLeast(1)
        val fullHeight = (width * BoardStickerPlacement.HEIGHT).roundToInt()
        val board = children.single().measure(Constraints.fixed(width, fullHeight))
        val top = (width * contentTop).roundToInt()
        layout((width * scale).roundToInt(), ((width * height - top) * scale).roundToInt()) {
            board.placeWithLayer(0, -(top * scale).roundToInt()) {
                transformOrigin = TransformOrigin(0f, 0f)
                scaleX = scale
                scaleY = scale
            }
        }
    }
}
