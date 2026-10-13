package com.kaanf.core.palette

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Luma-style cover backdrop: a vertical gradient from [BackdropPalette.base], three separately
 * blurred circles and a translucent theme overlay. The circle layers are recorded once per size /
 * palette and replayed every frame (GPU blur via RenderEffect / Skia; measured with no extra
 * scroll cost over a flat background, see README). Draws nothing while [palette] is null, so the
 * caller's flat background shows through; palette changes cross-fade (a dissolve, which is also
 * the reduce-motion friendly transition). Content goes on top of this, it is never blurred.
 *
 * [overlayColor] is multiplied by the reference alpha 0.45, raised only as far as needed to keep
 * [legibleTextColor] at [MIN_TEXT_CONTRAST]:1 on the brightest part of the backdrop.
 */
@Composable
fun CoverBackdrop(
    palette: BackdropPalette?,
    overlayColor: Color,
    legibleTextColor: Color,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = palette,
        modifier = modifier,
        animationSpec = tween(durationMillis = CROSSFADE_MILLIS),
        label = "CoverBackdrop",
    ) { target ->
        if (target == null) return@Crossfade
        val overlay = remember(target, overlayColor, legibleTextColor) { target.overlayFor(overlayColor, legibleTextColor) }
        LiveBackdrop(target, overlay)
    }
}

@Composable
private fun LiveBackdrop(palette: BackdropPalette, overlay: Color) {
    Spacer(
        Modifier.fillMaxSize().drawWithCache {
            val circles = palette.circles(size, pxPerDp = density)
            val layers = if (isRenderEffectBlurSupported) {
                val layerSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
                circles.map { circle -> obtainGraphicsLayer().apply { recordCircle(circle, this@drawWithCache, layoutDirection, layerSize) } }
            } else {
                null
            }
            onDrawBehind { drawBackdrop(palette, circles, layers, overlay) }
        },
    )
}

/** One circle in its own clipped layer: blur with CLAMP edges, clipped to the backdrop bounds. */
private fun GraphicsLayer.recordCircle(
    circle: BackdropCircle,
    density: Density,
    layoutDirection: LayoutDirection,
    size: IntSize,
) {
    renderEffect = BlurEffect(circle.blurRadius, circle.blurRadius, TileMode.Clamp)
    clip = true
    record(density, layoutDirection, size) { drawCircle(circle.color, circle.radius, circle.center) }
}

/** Gradient → circles (bottom to top) → overlay. [layers] null = no RenderEffect, radial approximation. */
private fun DrawScope.drawBackdrop(
    palette: BackdropPalette,
    circles: List<BackdropCircle>,
    layers: List<GraphicsLayer>?,
    overlay: Color,
) {
    val (top, bottom) = palette.baseGradient()
    drawRect(Brush.verticalGradient(listOf(top, bottom), startY = 0f, endY = size.height))
    if (layers != null) {
        layers.forEach { drawLayer(it) }
    } else {
        circles.forEach { drawRect(it.blurredDiscBrush()) }
    }
    drawRect(overlay)
}

internal data class BackdropCircle(
    val color: Color,
    val center: Offset,
    val radius: Float,
    /** RenderEffect/BlurEffect radius in px (both platforms use sigma = 0.57735 × radius + 0.5). */
    val blurRadius: Float,
)

/** Reference geometry for a backdrop of [size] px, in draw order (TopEnd, Middle, Bottom). */
internal fun BackdropPalette.circles(size: Size, pxPerDp: Float): List<BackdropCircle> {
    val w = size.width
    val h = size.height
    val topEndRadius = 0.35f * w
    return listOfNotNull(
        topEnd?.let {
            BackdropCircle(it.circleColor(), Offset(w - topEndRadius + topEndRadius / 3f, topEndRadius - topEndRadius / 3f), topEndRadius, 90f * pxPerDp)
        },
        middle?.let { BackdropCircle(it.circleColor(), Offset(w / 2f - w / 3f, h / 2f - w / 10f), 0.5f * w, 120f * pxPerDp) },
        bottom?.let { BackdropCircle(it.circleColor(), Offset(0.25f * w, h - 0.05f * w), 0.75f * w, 150f * pxPerDp) },
    )
}

/** Top/bottom gradient colours derived from the base colour in HSV (reference formulas). */
internal fun BackdropPalette.baseGradient(): Pair<Color, Color> {
    val hsv = base.toHsv()
    val value = hsv.value.coerceIn(0.2f, 0.85f)
    val bottomSaturation = if (hsv.saturation == 0f) 0f else min(1f, hsv.saturation / 2f + 0.75f)
    return Color.hsv(hsv.hue, hsv.saturation, value) to Color.hsv(hsv.hue, bottomSaturation, value * 0.75f)
}

/**
 * Overlay alpha multiplier: the reference 0.45, raised in 0.05 steps (max 0.9) until [textColor]
 * reaches [MIN_TEXT_CONTRAST] against every bright spot the backdrop can produce (gradient ends,
 * each circle and all circles stacked at their 0.5 alpha, before blur spreads them out).
 */
internal fun BackdropPalette.overlayAlpha(overlayColor: Color, textColor: Color): Float {
    val (gradientTop, gradientBottom) = baseGradient()
    val circleColors = listOfNotNull(topEnd, middle, bottom)
    val spots = listOf(gradientTop.toArgb(), gradientBottom.toArgb()).flatMap { gradient ->
        listOf(gradient, circleColors.fold(gradient) { acc, c -> blend(acc, c, CIRCLE_ALPHA) }) +
            circleColors.map { blend(gradient, it, CIRCLE_ALPHA) }
    }
    val overlay = overlayColor.copy(alpha = 1f).toArgb()
    val text = textColor.copy(alpha = 1f).toArgb()
    var alpha = REFERENCE_OVERLAY_ALPHA
    while (alpha < MAX_OVERLAY_ALPHA &&
        spots.any { contrastRatio(text, blend(it, overlay, alpha * overlayColor.alpha)) < MIN_TEXT_CONTRAST }
    ) {
        alpha += 0.05f
    }
    return min(alpha, MAX_OVERLAY_ALPHA)
}

internal fun BackdropPalette.overlayFor(overlayColor: Color, textColor: Color): Color =
    overlayColor.copy(alpha = overlayColor.alpha * overlayAlpha(overlayColor, textColor))

private fun Int.circleColor(): Color = Color(this).let { it.copy(alpha = it.alpha * CIRCLE_ALPHA) }

/**
 * Pre-Android 12 fallback: a radial gradient that approximates a Gaussian-blurred disc
 * (profile Φ((R − r) / σ)). Cheap and close, but it does not reproduce CLAMP edge spill.
 */
private fun BackdropCircle.blurredDiscBrush(): Brush {
    val sigma = 0.57735f * blurRadius + 0.5f
    val outer = radius + 3f * sigma
    val stops = Array(FALLBACK_STOPS) { i ->
        val fraction = i / (FALLBACK_STOPS - 1f)
        val coverage = normalCdf((radius - fraction * outer) / sigma)
        fraction to color.copy(alpha = color.alpha * coverage)
    }
    return Brush.radialGradient(*stops, center = center, radius = outer)
}

/** Φ(x) via the Abramowitz–Stegun 7.1.26 erf approximation (|error| < 1.5e-7). */
private fun normalCdf(x: Float): Float {
    val z = x / sqrt(2f)
    val t = 1f / (1f + 0.3275911f * abs(z))
    val poly = t * (0.2548296f + t * (-0.28449672f + t * (1.4214138f + t * (-1.4531521f + t * 1.0614054f))))
    val erf = 1f - poly * exp(-z * z)
    return 0.5f * (1f + if (z >= 0f) erf else -erf)
}

internal const val MIN_TEXT_CONTRAST = 4.5
internal const val REFERENCE_OVERLAY_ALPHA = 0.45f
private const val MAX_OVERLAY_ALPHA = 0.9f
private const val CIRCLE_ALPHA = 0.5f
private const val CROSSFADE_MILLIS = 400
private const val FALLBACK_STOPS = 12

/** Android < 12 ignores RenderEffect; iOS (Skia) always supports it. */
internal expect val isRenderEffectBlurSupported: Boolean
