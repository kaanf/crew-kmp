package com.kaanf.home.presentation.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.markImmutable
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt


fun Modifier.softShadow(
    cornerRadius: Dp,
    spread: Dp = 30.dp,
    offsetY: Dp = 12.dp,
    color: Color = Color.Black,
    maxAlpha: Float = 0.24f,
): Modifier = drawWithCache {
    val spreadPx = spread.toPx()
    val offsetPx = offsetY.toPx()
    val cornerPx = cornerRadius.toPx()

    // Kart, dış dikdörtgenin içinde yukarı yaslı duruyor: ışık yukarıdan geliyormuş gibi
    // gölge aşağıya daha çok taşsın.
    val outerWidth = size.width + spreadPx * 2f
    val outerHeight = size.height + spreadPx * 2f + offsetPx
    val scaleX = RASTER_SIZE / outerWidth
    val scaleY = RASTER_SIZE / outerHeight

    // Üst üste binen katmanların toplamı maxAlpha'ya varsın: geçiş üstel, yani bantsız.
    val stepAlpha = 1f - (1f - maxAlpha).pow(1f / STEPS)

    val shadow = ImageBitmap(RASTER_SIZE.toInt(), RASTER_SIZE.toInt())
    CanvasDrawScope().draw(
        density = this,
        layoutDirection = layoutDirection,
        canvas = Canvas(shadow),
        size = Size(RASTER_SIZE, RASTER_SIZE),
    ) {
        for (step in 0 until STEPS) {
            // t = 0 en dış katman, 1'e yaklaşan en iç. Karekök adımları içeride, yani
            // kartın kenarında sıklaştırıyor; dışarıda seyrek bırakıyor.
            //
            // Birikmiş alfa kartın kenarından uzaklığın karesiyle sönüyor: dış sınırda
            // eğim sıfıra iniyor, yani halenin bittiği yer görünmüyor. Doğrusal adımda
            // sönüm sabit eğimle sıfırlanıp belirgin bir yuvarlak dikdörtgen sınır
            // bırakıyor, kare adımda ise en dışta en dik hâline gelip daha da beter
            // oluyordu.
            val t = sqrt(step / STEPS.toFloat())
            val insetX = spreadPx * t
            val insetTop = spreadPx * t
            val insetBottom = (spreadPx + offsetPx) * t
            val radius = cornerPx + spreadPx * (1f - t)

            drawRoundRect(
                color = color.copy(alpha = stepAlpha),
                topLeft = Offset(insetX * scaleX, insetTop * scaleY),
                size = Size(
                    (outerWidth - insetX * 2f) * scaleX,
                    (outerHeight - insetTop - insetBottom) * scaleY,
                ),
                cornerRadius = CornerRadius(radius * scaleX, radius * scaleY),
            )
        }
    }
    shadow.markImmutable()

    val destinationOffset = IntOffset(-spreadPx.roundToInt(), -spreadPx.roundToInt())
    val destinationSize = IntSize(outerWidth.roundToInt(), outerHeight.roundToInt())
    val sourceSize = IntSize(RASTER_SIZE.toInt(), RASTER_SIZE.toInt())

    onDrawBehind {
        drawImage(
            image = shadow,
            srcOffset = IntOffset.Zero,
            srcSize = sourceSize,
            dstOffset = destinationOffset,
            dstSize = destinationSize,
            filterQuality = FilterQuality.Low,
        )
    }
}

private const val STEPS = 32

// ponytail: sabit raster; sönümün tamamı kartın dışındaki `spread` kadarlık dar şeride
// sığıyor, yani şeridin raster'daki genişliği ~ RASTER_SIZE * spread / (kart + 2*spread).
// 240dp kart + 12dp spread'de bu ~12 piksel. Daha dar bir spread ya da daha koyu bir
// maxAlpha basamaklanmayı görünür kılarsa çözüm STEPS değil (adımlar zaten piksel altına
// düşüyor) RASTER_SIZE'ı büyütmek — maliyeti boyut başına bir kez ödenen CPU raster'ı.
private const val RASTER_SIZE = 256f
