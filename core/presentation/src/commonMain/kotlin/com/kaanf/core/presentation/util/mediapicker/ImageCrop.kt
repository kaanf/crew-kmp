package com.kaanf.core.presentation.util.mediapicker

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Crops the square region [left], [top], [size] (all in [source] pixels) out of [source], scales it
 * to [outputSize] x [outputSize] and encodes it as WebP.
 *
 * The crop is drawn into an in-memory bitmap (lossless) and encoded exactly once, so there is no
 * double compression. The caller is responsible for keeping the crop rect within [source]'s bounds.
 */
suspend fun cropToSquareWebp(
    source: ImageBitmap,
    left: Int,
    top: Int,
    size: Int,
    outputSize: Int,
    quality: Int = 82,
): ByteArray = withContext(Dispatchers.Default) {
    val output = ImageBitmap(outputSize, outputSize)
    val canvas = Canvas(output)
    val paint = Paint().apply {
        isAntiAlias = true
        filterQuality = FilterQuality.High
    }
    canvas.drawImageRect(
        image = source,
        srcOffset = IntOffset(left, top),
        srcSize = IntSize(size, size),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(outputSize, outputSize),
        paint = paint,
    )
    encodeWebp(output, quality)
}
