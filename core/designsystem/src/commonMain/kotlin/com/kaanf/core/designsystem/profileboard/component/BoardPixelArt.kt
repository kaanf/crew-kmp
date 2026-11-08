package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults

internal val PixelInk = Color(0xFF121014)

/** A rectangle whose corners are cut in [steps] square stairs of [step] each, like pixel art. */
internal class PixelCornerShape(private val step: Dp, private val steps: Int = 3) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val s = with(density) { step.toPx() }
        val w = size.width
        val h = size.height
        val c = s * steps
        val path = Path().apply {
            moveTo(c, 0f)
            lineTo(w - c, 0f)
            for (i in 1..steps) { lineTo(w - c + s * (i - 1), s * i); lineTo(w - c + s * i, s * i) }
            lineTo(w, h - c)
            for (i in 1..steps) { lineTo(w - s * i, h - c + s * (i - 1)); lineTo(w - s * i, h - c + s * i) }
            lineTo(c, h)
            for (i in 1..steps) { lineTo(c - s * (i - 1), h - s * i); lineTo(c - s * i, h - s * i) }
            lineTo(0f, c)
            for (i in 1..steps) { lineTo(s * i, c - s * (i - 1)); lineTo(s * i, c - s * i) }
            close()
        }
        return Outline.Generic(path)
    }
}

internal val PixelTileShape = PixelCornerShape(4.dp)
internal val PixelChipShape = PixelCornerShape(3.dp, steps = 2)
internal val PixelBadgeShape = PixelCornerShape(3.dp, steps = 2)
internal val PixelArtShape = PixelCornerShape(2.dp, steps = 2)

/**
 * The board's pixel sticker: a white sticker outline, then dark, [accent] and dark pixel
 * frames around [content]. [frame] is the width of each coloured band.
 */
@Composable
internal fun PixelStickerFrame(
    accent: Color,
    modifier: Modifier = Modifier,
    shape: Shape = PixelTileShape,
    outline: Dp = 3.dp,
    frame: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(8.dp, shape, clip = false, ambientColor = Color.Black, spotColor = Color.Black)
            .background(AccessDefaults.TextPrimary, shape)
            .padding(outline)
            .background(PixelInk, shape)
            .padding(frame)
            .background(accent, shape)
            .padding(frame)
            .background(PixelInk, shape),
        content = content,
    )
}

/** One filled cell run of a pixel icon, in grid units. */
internal data class PixelRun(val x: Int, val y: Int, val width: Int = 1, val height: Int = 1, val color: Color)

/** Draws [runs] on a [columns] × [rows] grid scaled to [size]; decorative, so it has no semantics. */
@Composable
internal fun PixelIcon(runs: List<PixelRun>, columns: Int, rows: Int, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(width = size * columns / maxOf(columns, rows), height = size * rows / maxOf(columns, rows))
        .clearAndSetSemantics {}) {
        val cell = this.size.width / columns
        runs.forEach { run ->
            drawRect(run.color, Offset(run.x * cell, run.y * cell), Size(run.width * cell, run.height * cell))
        }
    }
}

internal fun pixelCross(color: Color) = listOf(0 to 0, 4 to 0, 1 to 1, 3 to 1, 2 to 2, 1 to 3, 3 to 3, 0 to 4, 4 to 4)
    .map { (x, y) -> PixelRun(x, y, color = color) }

internal val PixelFilm = listOf(
    PixelRun(0, 0, 12, 2, Color(0xFFC7A3F7)),
    PixelRun(0, 10, 12, 2, Color(0xFFC7A3F7)),
    PixelRun(0, 2, 2, 8, Color(0xFFC7A3F7)),
    PixelRun(10, 2, 2, 8, Color(0xFFC7A3F7)),
    PixelRun(2, 5, 8, 2, Color(0xFFC7A3F7)),
)

private val SpotifyGreen = Color(0xFF3BE256)

internal val PixelSpotify: List<PixelRun> = listOf(
    PixelRun(4, 0, 6, 1, SpotifyGreen), PixelRun(2, 1, 10, 1, SpotifyGreen), PixelRun(1, 2, 12, 2, SpotifyGreen),
    PixelRun(0, 4, 14, 6, SpotifyGreen), PixelRun(1, 10, 12, 2, SpotifyGreen), PixelRun(2, 12, 10, 1, SpotifyGreen),
    PixelRun(4, 13, 6, 1, SpotifyGreen),
    PixelRun(3, 4, color = PixelInk), PixelRun(4, 3, 6, 1, PixelInk), PixelRun(10, 4, color = PixelInk),
    PixelRun(4, 7, color = PixelInk), PixelRun(5, 6, 4, 1, PixelInk), PixelRun(9, 7, color = PixelInk),
    PixelRun(5, 10, color = PixelInk), PixelRun(6, 9, 3, 1, PixelInk), PixelRun(9, 10, color = PixelInk),
)
