package com.kaanf.core.designsystem.modifier

import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.kaanf.core.designsystem.theme.AccessShapes
import kotlin.math.abs
import kotlin.random.Random

private const val MinTiltDegrees = 5f
private const val MaxTiltDegrees = 11f
private const val NeighbourScale = 0.78f
private const val NeighbourAlpha = 0.55f
private const val JitterScale = 0.03f
private val NeighbourPull = 44.dp
private val CenterElevation = 16.dp
private val JitterX = 12.dp
private val JitterY = 6.dp

private data class CardJitter(val tilt: Float, val dx: Float, val dy: Float)

// Sayfa başına sabit (seed = page): kartlar her kompozisyonda aynı açıyla durur.
private fun jitterFor(page: Int): CardJitter {
    val random = Random(page)
    val magnitude = MinTiltDegrees + random.nextFloat() * (MaxTiltDegrees - MinTiltDegrees)
    return CardJitter(
        tilt = if (random.nextBoolean()) magnitude else -magnitude,
        dx = random.nextFloat() * 2f - 1f,
        dy = random.nextFloat() * 2f - 1f,
    )
}

/**
 * Carousel kartı: ortadaki tam boy, komşular eğik/küçük/soluk. Yalnız graphicsLayer —
 * layout boyutu değişmediği için içerideki drawWithCache (ör. gradient kartlar) kaydırırken yeniden raster olmaz.
 * Ortadaki kartın komşuların üstünde kalması için çağıran zincirin başına zIndex koyar.
 */
fun Modifier.carouselPage(
    pagerState: PagerState,
    page: Int,
    shape: Shape = AccessShapes.Large,
): Modifier {
    val jitter = jitterFor(page)
    return graphicsLayer {
        val offset = (
            (page - pagerState.currentPage) - pagerState.currentPageOffsetFraction
            ).coerceIn(-1f, 1f)
        val distance = abs(offset)

        rotationZ = distance * jitter.tilt
        transformOrigin = TransformOrigin(0.5f, 0.5f)
        translationX = -offset * NeighbourPull.toPx() + distance * jitter.dx * JitterX.toPx()
        translationY = distance * jitter.dy * JitterY.toPx()

        val scale = lerp(1f, NeighbourScale + jitter.dx * JitterScale, distance)
        scaleX = scale
        scaleY = scale
        alpha = lerp(1f, NeighbourAlpha, distance)

        this.shape = shape
        clip = true
        shadowElevation = lerp(CenterElevation.toPx(), 0f, distance)
    }
}
