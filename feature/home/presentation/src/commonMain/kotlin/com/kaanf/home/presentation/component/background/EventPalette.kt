package com.kaanf.home.presentation.component.background

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import kotlin.math.abs
import kotlin.math.min

/**
 * Etkinlik görselinden arka planın kullanacağı renkleri çıkarır. Görselin kendisi hiçbir
 * yerde çizilmez; tek çıktısı shader'a gidecek [PALETTE_SIZE] adet renktir.
 *
 * Yöntem: pikselleri ton (hue) kutularına doldur, doygun pikselleri daha ağır say (kontur
 * ve kağıt rengi paleti belirlemesin), en kalabalık ve birbirinden yeterince uzak tonları
 * seç, sonra her yuvayı hedef parlaklık/doygunluğa oturt. Çamurlu palet böyle engelleniyor.
 */
internal const val PALETTE_SIZE = 5

/** Shader uniform'ları için düz dizi: [r, g, b] * [PALETTE_SIZE]. */
internal fun ImageBitmap.extractPalette(): FloatArray = extractPalette(toPixelMap().buffer)

internal fun extractPalette(pixels: IntArray): FloatArray {
    val binWeight = FloatArray(HUE_BINS)
    val binHueSin = FloatArray(HUE_BINS)
    val binHueCos = FloatArray(HUE_BINS)
    val binChroma = FloatArray(HUE_BINS)
    var totalWeight = 0f
    // Açıklık bütün piksellerden ölçülür. Ton için koyu pikselleri elemek zorundayız
    // (orada ton gürültü), ama aynı eleme parlaklığa uygulanırsa koyu bir görsel
    // olduğundan açık ölçülür ve arka plan griye kaçar.
    var lightnessSum = 0f
    var lightnessCount = 0

    for (argb in pixels) {
        val r = ((argb shr 16) and 0xFF) / 255f
        val g = ((argb shr 8) and 0xFF) / 255f
        val b = (argb and 0xFF) / 255f

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val lightness = (max + min) / 2f
        lightnessSum += lightness
        lightnessCount++

        // Kontur siyahı ve patlamış beyaz renk taşımaz, sadece kontrast taşır.
        if (lightness < 0.08f || lightness > 0.94f) continue

        val chroma = if (max < 1e-5f) 0f else (max - min) / max
        val weight = 0.2f + chroma

        val hue = hueOf(r, g, b, max, max - min)
        val bin = ((hue / 360f) * HUE_BINS).toInt().coerceIn(0, HUE_BINS - 1)
        binWeight[bin] += weight
        // Ton dairesel: kutu içi ortalamayı açı olarak topluyoruz.
        val radians = hue * DEGREES_TO_RADIANS
        binHueSin[bin] += kotlin.math.sin(radians) * weight
        binHueCos[bin] += kotlin.math.cos(radians) * weight
        binChroma[bin] += chroma * weight
        totalWeight += weight
    }

    val imageLightness = if (lightnessCount > 0) lightnessSum / lightnessCount else DEFAULT_LIGHTNESS
    if (totalWeight <= 0f) return neutralPalette(imageLightness)
    val seeds = pickSeeds(binWeight, binHueSin, binHueCos, binChroma, totalWeight)
    // Hiçbir kutu gerçek renk taşımıyorsa (gri/siyah beyaz görsel) uydurma ton üretmeyiz.
    if (seeds.isEmpty()) return neutralPalette(imageLightness)

    val baseLightness = imageLightness.coerceIn(MIN_BASE_LIGHTNESS, MAX_BASE_LIGHTNESS)
    val palette = FloatArray(PALETTE_SIZE * 3)
    // Vurgular baskın rengi geçemesin (aşağıda); ölçüt baskın yuvanın parlaklığı.
    var dominantLuma = 1f
    for (slot in 0 until PALETTE_SIZE) {
        val exact = seeds.getOrNull(slot)
        val base = exact ?: seeds[slot % seeds.size]
        val hue = if (exact != null) base.hue else (base.hue + HUE_SPREAD[slot] + 360f) % 360f
        val target = SLOT_TARGETS[slot]
        // Doygunluk görselin kendi karakterinden gelir; yuva sadece yön verir. Sabit
        // değerler her görsele aynı paleti verdiği için burada ölçülen değer kullanılır.
        val saturation = (base.chroma * target.saturationGain).coerceIn(MIN_SATURATION, MAX_SATURATION)
        // Yuva açıklığı mutlak değil, görselin kendi açıklığına göre kayma. Sabit hedefler
        // koyu görseli de orta tona çekiyordu.
        var lightness = (baseLightness + target.lightnessOffset + hueLightnessLift(hue, saturation))
            .coerceIn(MIN_LIGHTNESS, MAX_LIGHTNESS)
        var rgb = hslToRgb(hue, saturation, lightness)

        // Hiçbir yuva baskın rengi gölgede bırakmasın. Mavi bir kapakta baskın renk luma
        // 22'de kalırken HUE_SPREAD'in uydurduğu sarı 74'e çıkıyor ve alan sarı okunuyordu;
        // aynı şey gerçek ama parlak bir vurgu tohumu için de geçerli, o yüzden kural
        // uydurma/gerçek ayrımı yapmıyor. Tavan HSL açıklığında değil luma'da: sarı L=0.42'de
        // bile parlak, açıklığı kısmak sorunu çözmüyor.
        if (slot > 0) {
            val ceiling = dominantLuma * ACCENT_LUMA_GAIN + ACCENT_LUMA_HEADROOM
            // Luma, L'de tekdüze artıyor; birkaç oran düzeltmesi hedefe yeterince yaklaşıyor.
            repeat(3) {
                val luma = lumaOf(rgb)
                if (luma > ceiling) {
                    lightness = (lightness * ceiling / luma).coerceAtLeast(MIN_LIGHTNESS)
                    rgb = hslToRgb(hue, saturation, lightness)
                }
            }
        }

        if (slot == 0) dominantLuma = lumaOf(rgb)
        rgb.copyInto(palette, slot * 3)
    }
    return palette
}

private fun lumaOf(rgb: FloatArray): Float =
    0.2126f * rgb[0] + 0.7152f * rgb[1] + 0.0722f * rgb[2]

/**
 * HSL açıklığı ton-kör: aynı L değerinde saf mavinin Rec.709 luma'sı 0.07, sarınınki 0.93.
 * Shader zemini 0.30-0.56 ile çarpınca mavi/mor/kırmızı siyaha gömülüyor, sarı-yeşil okunmaya
 * devam ediyor — "mavi neden belli olmuyor" bunun sonucu. Karanlık tonların açıklığını
 * yukarı itiyoruz; açık tonlara dokunmuyoruz (tek yönlü), yoksa sıcak paletler sönükleşirdi.
 * Doygunlukla ölçekleniyor: nötre yakın renkler yerinde kalır.
 */
private fun hueLightnessLift(hue: Float, saturation: Float): Float {
    val hueLuma = lumaOf(hslToRgb(hue, 1f, 0.5f))
    if (hueLuma >= NEUTRAL_HUE_LUMA) return 0f
    return LUMA_LIFT * (NEUTRAL_HUE_LUMA - hueLuma) * saturation
}

private class Seed(val hue: Float, val chroma: Float)

/**
 * Yalnız kayda değer paya ve gerçek renkliliğe sahip kutular tohum olur. Nötr pikseller
 * ton çemberine rastgele dağılıp her kutuya benzer bir taban bırakıyor; bu eşik olmadan
 * palet görselden değil o gürültüden doğuyor ve her görselde aynı çıkıyor.
 */
private fun pickSeeds(
    binWeight: FloatArray,
    binHueSin: FloatArray,
    binHueCos: FloatArray,
    binChroma: FloatArray,
    totalWeight: Float,
): List<Seed> {
    val minWeight = totalWeight * MIN_BIN_SHARE
    val picked = mutableListOf<Seed>()

    for (bin in binWeight.indices.sortedByDescending { binWeight[it] }) {
        if (binWeight[bin] < minWeight) break
        val chroma = binChroma[bin] / binWeight[bin]
        if (chroma < MIN_BIN_CHROMA) continue
        val hue = (kotlin.math.atan2(binHueSin[bin], binHueCos[bin]) / DEGREES_TO_RADIANS + 360f) % 360f
        if (picked.none { hueDistance(it.hue, hue) < MIN_HUE_DISTANCE }) {
            picked += Seed(hue, chroma)
            if (picked.size == PALETTE_SIZE) break
        }
    }
    return picked
}

/** Renksiz görsel renksiz alan verir; görselin kendi açıklığı korunur. */
private fun neutralPalette(imageLightness: Float): FloatArray {
    val base = imageLightness.coerceIn(0.10f, 0.52f)
    val palette = FloatArray(PALETTE_SIZE * 3)
    for (slot in 0 until PALETTE_SIZE) {
        val value = (base + NEUTRAL_LIGHTNESS_OFFSETS[slot]).coerceIn(0.06f, 0.72f)
        palette[slot * 3] = value
        palette[slot * 3 + 1] = value * 0.97f
        palette[slot * 3 + 2] = value * 0.93f
    }
    return palette
}

internal fun FloatArray.paletteColor(index: Int): Color =
    Color(red = this[index * 3], green = this[index * 3 + 1], blue = this[index * 3 + 2])

private fun hueDistance(a: Float, b: Float): Float {
    val diff = abs(a - b)
    return min(diff, 360f - diff)
}

private fun hueOf(r: Float, g: Float, b: Float, max: Float, delta: Float): Float {
    if (delta < 1e-5f) return 0f
    val hue = when (max) {
        r -> 60f * (((g - b) / delta) % 6f)
        g -> 60f * (((b - r) / delta) + 2f)
        else -> 60f * (((r - g) / delta) + 4f)
    }
    return (hue + 360f) % 360f
}

private fun hslToRgb(hue: Float, saturation: Float, lightness: Float): FloatArray {
    val c = (1f - abs(2f * lightness - 1f)) * saturation
    val h = hue / 60f
    val x = c * (1f - abs((h % 2f) - 1f))
    val m = lightness - c / 2f
    val (r, g, b) = when {
        h < 1f -> Triple(c, x, 0f)
        h < 2f -> Triple(x, c, 0f)
        h < 3f -> Triple(0f, c, x)
        h < 4f -> Triple(0f, x, c)
        h < 5f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return floatArrayOf(
        (r + m).coerceIn(0f, 1f),
        (g + m).coerceIn(0f, 1f),
        (b + m).coerceIn(0f, 1f),
    )
}

private class SlotTarget(val lightnessOffset: Float, val saturationGain: Float)

private const val HUE_BINS = 18
private const val MIN_HUE_DISTANCE = 28f
private const val DEGREES_TO_RADIANS = 0.017453292f
private const val DEFAULT_LIGHTNESS = 0.28f

/** Karanlık tonların açıklık telafisi; 0 = kapalı, 1 = tam algısal eşitleme. */
private const val LUMA_LIFT = 0.55f
private const val NEUTRAL_HUE_LUMA = 0.5f

/**
 * Vurgu yuvalarının parlaklık tavanı: baskın rengin luma'sının bu katı + sabit pay. Kural
 * yalnız baskın renk koyuyken devreye giriyor, açık paletlerde tavan zaten aşılmıyor.
 */
private const val ACCENT_LUMA_GAIN = 1.8f
private const val ACCENT_LUMA_HEADROOM = 0.05f

/** Bir kutunun tohum sayılması için gereken pay ve renklilik eşiği. Ayar düğmesi burası. */
private const val MIN_BIN_SHARE = 0.045f
private const val MIN_BIN_CHROMA = 0.16f

// Tohum zaten MIN_BIN_CHROMA eşiğini geçti, yani orada gerçek bir renk var: taban
// doygunluğu uydurma değil, ortalamaya kaçıp grileşmeyi engelliyor.
private const val MIN_SATURATION = 0.34f
private const val MAX_SATURATION = 0.94f
private const val MIN_LIGHTNESS = 0.07f
private const val MAX_LIGHTNESS = 0.74f
private const val MIN_BASE_LIGHTNESS = 0.13f
private const val MAX_BASE_LIGHTNESS = 0.54f

/** Sırasıyla: baskın, ikincil, canlı vurgu, açık destek, koyu destek. */
private val SLOT_TARGETS = arrayOf(
    SlotTarget(lightnessOffset = -0.03f, saturationGain = 1.05f),
    SlotTarget(lightnessOffset = 0.05f, saturationGain = 1.15f),
    SlotTarget(lightnessOffset = 0.14f, saturationGain = 1.45f),
    SlotTarget(lightnessOffset = 0.21f, saturationGain = 0.90f),
    SlotTarget(lightnessOffset = -0.11f, saturationGain = 1.05f),
)

/** Yeterince ayrı ton bulunamayan yuvalar tohumun çevresine yayılır. */
private val HUE_SPREAD = floatArrayOf(0f, 32f, -36f, 58f, -60f)

private val NEUTRAL_LIGHTNESS_OFFSETS = floatArrayOf(-0.05f, 0f, 0.10f, 0.17f, -0.11f)
