package com.kaanf.core.palette

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/** WCAG relative luminance of an opaque sRGB colour. */
internal fun Int.relativeLuminance(): Double =
    0.2126 * linearChannel(16) + 0.7152 * linearChannel(8) + 0.0722 * linearChannel(0)

internal fun contrastRatio(a: Int, b: Int): Double {
    val la = a.relativeLuminance()
    val lb = b.relativeLuminance()
    return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
}

/** Source-over of [top] (with [alpha]) on opaque [bottom], in gamma-encoded sRGB like Compose/Skia. */
internal fun blend(bottom: Int, top: Int, alpha: Float): Int {
    fun mix(shift: Int): Int {
        val lower = (bottom shr shift) and 0xFF
        val upper = (top shr shift) and 0xFF
        return (lower + (upper - lower) * alpha + 0.5f).toInt().coerceIn(0, 255)
    }
    return (0xFF shl 24) or (mix(16) shl 16) or (mix(8) shl 8) or mix(0)
}

/** RGB → HSV with hue in degrees [0, 360), saturation and value in [0, 1]. */
internal data class Hsv(val hue: Float, val saturation: Float, val value: Float)

internal fun Int.toHsv(): Hsv {
    val r = ((this shr 16) and 0xFF) / 255f
    val g = ((this shr 8) and 0xFF) / 255f
    val b = (this and 0xFF) / 255f
    val maxC = max(r, max(g, b))
    val delta = maxC - min(r, min(g, b))
    val hue = when {
        delta == 0f -> 0f
        maxC == r -> 60f * (((g - b) / delta).mod(6f))
        maxC == g -> 60f * ((b - r) / delta + 2f)
        else -> 60f * ((r - g) / delta + 4f)
    }
    val saturation = if (maxC == 0f) 0f else delta / maxC
    return Hsv(hue, saturation, maxC)
}

private fun Int.linearChannel(shift: Int): Double {
    val c = ((this shr shift) and 0xFF) / 255.0
    return if (c <= 0.04045) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
}
