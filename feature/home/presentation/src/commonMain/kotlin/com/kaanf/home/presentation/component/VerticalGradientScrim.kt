package com.kaanf.home.presentation.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/**
 * Dikey gradient'i her karede shader olarak boyamak yerine bir kez bitmap'e rasterize edip
 * basar. Skia'da hareket eden gradient (scroll, sayfa geçişi) kare düşürüyor; hazır bitmap'i
 * basmak ucuz bir textured blit.
 *
 * Gradient dikey olduğu için her satır tek renk: tam genişlikte rasterize etmeye gerek yok,
 * birkaç piksel genişliğinde üretip enine geriyoruz.
 */
fun Modifier.verticalGradientScrim(brush: Brush): Modifier = drawWithCache {
    val height = size.height.toInt().coerceAtLeast(1)
    val scrim = ImageBitmap(RASTER_WIDTH, height)
    CanvasDrawScope().draw(
        density = this,
        layoutDirection = layoutDirection,
        canvas = Canvas(scrim),
        size = Size(RASTER_WIDTH.toFloat(), size.height),
    ) {
        drawRect(brush)
    }

    val source = IntSize(RASTER_WIDTH, height)
    val destination = IntSize(size.width.toInt().coerceAtLeast(1), height)

    onDrawBehind {
        drawImage(
            image = scrim,
            srcOffset = IntOffset.Zero,
            srcSize = source,
            dstOffset = IntOffset.Zero,
            dstSize = destination,
            filterQuality = FilterQuality.Low,
        )
    }
}

// Kenar örneklemesinde bulanıklık olmasın diye 1 değil; maliyeti yine ihmal edilebilir.
private const val RASTER_WIDTH = 4
