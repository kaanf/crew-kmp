package com.kaanf.home.presentation.component.background

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.asPainter
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.size.Precision
import com.kaanf.core.designsystem.theme.AccessDefaults

/**
 * Etkinlik görselinden türeyen renk alanı.
 *
 * Boru hattı: görsel -> [PALETTE_SAMPLE_SIZE] px örnek (bir kez, Coil) -> [PALETTE_SIZE]
 * renklik palet -> tek geçişli AGSL/SkSL shader (5 geniş Gauss lekesi + alan bükümü +
 * grain + taban rengine sönüm).
 *
 * Görselin kendisi hiçbir katmanda çizilmez; kullanıcı yalnız ondan türeyen renkleri görür.
 *
 * Alan durgun: zaman uniform'u yok, dolayısıyla CPU tarafında çizim komutu ancak katman
 * geçersiz kılındığında yeniden kaydediliyor ve shader kurulumu boyut başına bir kez yapılıyor.
 * Dikkat: fragment shader'ın *kendisi* display list her rasterize edildiğinde çalışır — Android'de
 * HWUI'nin damage scissor'ı bunu sınırlar, Skiko/iOS'ta sahne her karede baştan çizildiği için
 * sınırlamaz.
 */
@Composable
fun DynamicEventBackground(
    imageUrl: String,
    modifier: Modifier = Modifier,
    /** Renk alanının tam güçte olduğu son nokta (çizim alanı yüksekliğinin oranı). */
    fadeOutStart: Float = 0.52f,
    /** Renk alanının tamamen [baseColor]'a döndüğü nokta. */
    fadeOutEnd: Float = 1f,
    baseColor: Color = AccessDefaults.Background,
) {
    // Palet çıkana kadar hiçbir şey çizmiyoruz: altta zaten AppScaffold'un arka planı var.
    val colors = rememberEventPalette(imageUrl) ?: return

    val effect = remember(colors, baseColor, fadeOutStart, fadeOutEnd) {
        createArtworkShaderEffect(colors, baseColor, fadeOutStart, fadeOutEnd)
    }
    // Shader yoksa alan bir kez rasterize edilir.
    val staticField = remember(colors, baseColor, effect) {
        if (effect == null) rasterizeField(colors, baseColor) else null
    }
    val paint = remember { Paint() }

    Box(
        // drawBehind değil drawWithCache: shader yalnız boyuta bağlı, karede değişen bir
        // uniform yok. Kurulumu cache bloğuna alınca `shader()` çizim başına değil boyut
        // başına bir kez çağrılıyor (iOS'ta bu çağrı native bir Skia nesnesi üretiyordu).
        modifier.drawWithCache {
            if (staticField != null) {
                val source = IntSize(FIELD_RASTER_SIZE, FIELD_RASTER_SIZE)
                val destination = IntSize(
                    size.width.toInt().coerceAtLeast(1),
                    size.height.toInt().coerceAtLeast(1),
                )
                return@drawWithCache onDrawBehind {
                    drawImage(
                        image = staticField,
                        srcSize = source,
                        dstSize = destination,
                        filterQuality = FilterQuality.Low,
                    )
                }
            }

            paint.shader = effect?.shader(size)
            val width = size.width
            val height = size.height
            onDrawBehind {
                drawIntoCanvas { canvas ->
                    canvas.drawRect(0f, 0f, width, height, paint)
                }
            }
        },
    )
}

/**
 * Görselin paletini çıkarır ve hazır olunca yayınlar. Coil'in bellek önbelleği sayesinde
 * aynı etkinliğe dönüldüğünde görsel tekrar inmez.
 */
@Composable
private fun rememberEventPalette(imageUrl: String?): FloatArray? {
    val platformContext = LocalPlatformContext.current
    var palette by remember(imageUrl) { mutableStateOf<FloatArray?>(null) }

    LaunchedEffect(imageUrl) {
        palette = imageUrl?.let { loadPalette(platformContext, it) }
    }

    return palette
}

/**
 * Görselden paleti çıkarır. Tam boy kapak arka plan için asla decode edilmez; Coil'in
 * bellek önbelleği sayesinde aynı etkinliğe dönüldüğünde tekrar inmez.
 */
private suspend fun loadPalette(
    context: PlatformContext,
    imageUrl: String,
): FloatArray? {
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        // Hero'nun tam boy kaydıyla çakışmasın diye ayrı anahtar.
        .memoryCacheKey("dynamic-background:$imageUrl")
        .size(PALETTE_SAMPLE_SIZE, PALETTE_SAMPLE_SIZE)
        .precision(Precision.INEXACT)
        .disallowHardwareBitmap()
        .build()

    val image = (SingletonImageLoader.get(context).execute(request) as? SuccessResult)
        ?.image
        ?: return null

    val side = PALETTE_SAMPLE_SIZE.toFloat()
    val sample = ImageBitmap(PALETTE_SAMPLE_SIZE, PALETTE_SAMPLE_SIZE)
    val painter = image.asPainter(context)
    CanvasDrawScope().draw(
        density = Density(1f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(sample),
        size = Size(side, side),
    ) {
        with(painter) { draw(Size(side, side)) }
    }
    return sample.extractPalette()
}

/**
 * Android < 33 yedeği: runtime shader yok. Aynı lekeleri küçük bir bitmap'e bir kez
 * rasterize edip her karede gerdirerek basıyoruz — kare başına tahsis yok, animasyon yok.
 * Radial gradient'ler zaten yumuşak olduğu için büyütme bulanıklığı sorun değil.
 */
private fun rasterizeField(palette: FloatArray, baseColor: Color): ImageBitmap {
    val side = FIELD_RASTER_SIZE.toFloat()
    val field = ImageBitmap(FIELD_RASTER_SIZE, FIELD_RASTER_SIZE)
    CanvasDrawScope().draw(
        density = Density(1f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(field),
        size = Size(side, side),
    ) {
        drawRect(palette.paletteColor(0))
        FIELD_CENTERS.forEachIndexed { slot, center ->
            drawRect(
                Brush.radialGradient(
                    0f to palette.paletteColor(slot),
                    1f to Color.Transparent,
                    center = Offset(center.first * side, center.second * side),
                    radius = side * FIELD_RADIUS,
                ),
            )
        }
        // Shader'daki taban rengine sönümün karşılığı. Raster kare olup gerdirildiği için
        // burada oran cinsinden; yedek yol olduğundan bu yaklaşım yeterli.
        drawRect(
            Brush.verticalGradient(
                0.00f to Color.Transparent,
                0.34f to Color.Transparent,
                0.70f to baseColor,
                1.00f to baseColor,
            ),
        )
    }
    return field
}

private const val FIELD_RASTER_SIZE = 128
private const val FIELD_RADIUS = 0.62f

/** Shader'daki leke merkezlerinin durgun hâli. */
private val FIELD_CENTERS = listOf(
    0.24f to 0.14f,
    0.82f to 0.32f,
    0.46f to 0.58f,
    0.14f to 0.84f,
    0.88f to 0.96f,
)

