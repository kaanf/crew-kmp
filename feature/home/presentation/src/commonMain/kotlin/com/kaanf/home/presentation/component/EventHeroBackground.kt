package com.kaanf.home.presentation.component

import androidx.compose.foundation.border
import com.kaanf.core.designsystem.markImmutable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.min

fun Modifier.eventHeroBackground(
    shape: Shape = RoundedCornerShape(20.dp)
): Modifier {
    return this
        .clip(shape)
        .drawWithCache {
            // Gradient'ler yumuşak: tam çözünürlükte rasterize etmeye gerek yok. Tam boyda
            // ~1170x600 px'lik bir ImageBitmap (~2.8 MB) ve 4 tam boy gradient dolgusu
            // (~2.8M piksel yazımı) main thread'de ödeniyordu; küçük boyayıp büyütmek gözle
            // ayırt edilmiyor ama maliyeti ~30 kat düşürüyor. (Aynı desen verticalGradientScrim,
            // GradientChallengeCard ve softShadow'da da kullanılıyor.)
            val rasterScale = RASTER_PX / size.width
            val rasterWidth = RASTER_PX
            val rasterHeight = (size.height * rasterScale).coerceAtLeast(1f)
            val minSize = min(rasterWidth, rasterHeight)

            val baseGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    0.00f to Color(0xFF21170F),
                    0.30f to Color(0xFF1A130D),
                    0.55f to Color(0xFF130F0A),
                    0.78f to Color(0xFF0F0B08),
                    1.00f to Color(0xFF0B0805),
                ),
                start = Offset(rasterWidth * 0.15f, 0f),
                end = Offset(rasterWidth * 0.85f, rasterHeight)
            )

            val coralGlow = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to Color(0xFFFF7A5C).copy(alpha = 0.20f),
                    0.24f to Color(0xFFFF7A5C).copy(alpha = 0.13f),
                    0.48f to Color(0xFFFF7A5C).copy(alpha = 0.065f),
                    0.72f to Color(0xFFFF7A5C).copy(alpha = 0.025f),
                    1.00f to Color.Transparent,
                ),
                center = Offset(rasterWidth * 0.98f, rasterHeight * 0.02f),
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
                center = Offset(rasterWidth * 0.02f, rasterHeight * 1.02f),
                radius = minSize * 1.64f
            )

            val centerDark = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to Color.Black.copy(alpha = 0.18f),
                    0.45f to Color.Black.copy(alpha = 0.10f),
                    0.75f to Color.Black.copy(alpha = 0.04f),
                    1.00f to Color.Transparent,
                ),
                center = Offset(rasterWidth * 0.5f, rasterHeight * 0.52f),
                radius = minSize * 0.55f
            )

            val sourceWidth = rasterWidth.toInt().coerceAtLeast(1)
            val sourceHeight = rasterHeight.toInt().coerceAtLeast(1)
            val cachedImage = ImageBitmap(sourceWidth, sourceHeight)
            CanvasDrawScope().draw(
                density = this,
                layoutDirection = layoutDirection,
                canvas = Canvas(cachedImage),
                size = Size(rasterWidth, rasterHeight),
            ) {
                drawRect(baseGradient)
                drawRect(coralGlow)
                drawRect(limeGlow)
                drawRect(centerDark)
            }
            cachedImage.markImmutable()

            val source = IntSize(sourceWidth, sourceHeight)
            val destination = IntSize(
                size.width.toInt().coerceAtLeast(1),
                size.height.toInt().coerceAtLeast(1),
            )

            onDrawBehind {
                drawImage(
                    image = cachedImage,
                    srcOffset = IntOffset.Zero,
                    srcSize = source,
                    dstOffset = IntOffset.Zero,
                    dstSize = destination,
                    filterQuality = FilterQuality.Low,
                )
            }
        }
        .border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.12f),
            shape = shape
        )
}

// Kart @3x'te ~1170px genişliğinde; yumuşak gradient için 160px raster + upscale yeterli.
private const val RASTER_PX = 160f
