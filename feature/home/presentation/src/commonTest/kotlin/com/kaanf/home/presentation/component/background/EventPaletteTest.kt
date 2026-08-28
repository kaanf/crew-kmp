package com.kaanf.home.presentation.component.background

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventPaletteTest {

    /** Gerçekçi girdi: piksellerin bir kısmı nötr gürültü, kalanı iki gerçek renk. */
    private fun photo(base: Int, accent: Int, neutralRatio: Float): IntArray {
        var seed = 12345
        return IntArray(2304) {
            seed = seed * 1103515245 + 12345
            val roll = ((seed shr 16) and 0x7FFF) / 32767f
            when {
                roll < neutralRatio -> {
                    val value = 20 + ((seed shr 8) and 0x3F)
                    (0xFF shl 24) or (value shl 16) or (value shl 8) or value
                }
                roll < neutralRatio + (1f - neutralRatio) * 0.75f -> base
                else -> accent
            }
        }
    }

    @Test
    fun different_photos_produce_different_palettes() {
        // Asıl regresyon: nötr pikseller ton çemberine dağıldığı için palet görselden
        // değil gürültüden doğuyordu ve her görselde aynı çıkıyordu.
        val blue = extractPalette(photo(0xFF1B3A8C.toInt(), 0xFFC02030.toInt(), 0.45f))
        val orange = extractPalette(photo(0xFFD2691E.toInt(), 0xFF1E8C8C.toInt(), 0.45f))

        assertEquals(PALETTE_SIZE * 3, blue.size)
        assertTrue(blue.all { it in 0f..1f } && orange.all { it in 0f..1f })
        assertTrue(blue[2] > blue[0], "mavi görselde baskın renk maviye çalmalı")
        assertTrue(orange[0] > orange[2], "turuncu görselde baskın renk sıcak olmalı")
        assertTrue(distance(blue, orange, slot = 0) > 0.3f, "baskın renkler ayrışmalı")
        assertTrue(distance(blue, orange, slot = 2) > 0.3f, "vurgu renkleri ayrışmalı")
    }

    @Test
    fun dark_photo_stays_dark() {
        // Regresyon: koyu pikseller ton gürültüsü yüzünden elenirken parlaklık ölçümünden
        // de düşüyordu, koyu görsel orta tonlu gri bir alan üretiyordu.
        val dark = extractPalette(photo(0xFF1B0A2E.toInt(), 0xFF2E1410.toInt(), 0.55f))
        val bright = extractPalette(photo(0xFF7FB0FF.toInt(), 0xFFFFD08A.toInt(), 0.10f))

        assertTrue(meanLuma(dark) < 0.34f, "koyu görsel koyu alan vermeli")
        assertTrue(meanLuma(bright) - meanLuma(dark) > 0.15f, "koyu ve açık ayrışmalı")
        assertTrue(meanSpread(dark) > 0.10f, "koyu alan renkli kalmalı, gri değil")
    }

    @Test
    fun dark_hues_stay_visible() {
        // Regresyon: HSL açıklığı ton-kör. Aynı L'de mavinin Rec.709 luma'sı 0.07, sarınınki
        // 0.93; telafi olmadan mavi baskın renk luma 0.15'te kalıyor, shader'ın koyu zemin
        // çarpanından (0.30) sonra siyaha gömülüyor ve "mavi belli olmuyor" oluyordu.
        val blue = extractPalette(photo(0xFF1B3A8C.toInt(), 0xFFC02030.toInt(), 0.45f))
        val orange = extractPalette(photo(0xFFD2691E.toInt(), 0xFF1E8C8C.toInt(), 0.45f))

        assertTrue(luma(blue, slot = 0) > 0.18f, "mavi baskın renk çok koyu")
        assertTrue(
            luma(blue, slot = 0) > luma(orange, slot = 0) * 0.6f,
            "soğuk baskın renk sıcak olana göre sönük kalıyor",
        )
    }

    @Test
    fun accents_do_not_outshine_the_dominant_colour() {
        // Regresyon: yeterli tohum yokken HUE_SPREAD uydurma ton üretiyor, üstüne pozitif
        // açıklık kayması binince alanın en parlak parçası oluyordu — mavi bir kapakta
        // baskın renk 15'te kalırken uydurma sarı 74'e çıkıyordu.
        for (palette in listOf(
            extractPalette(photo(0xFF1B3A8C.toInt(), 0xFFC02030.toInt(), 0.45f)),
            extractPalette(photo(0xFFD2691E.toInt(), 0xFF1E8C8C.toInt(), 0.45f)),
            extractPalette(photo(0xFF1B0A2E.toInt(), 0xFF2E1410.toInt(), 0.55f)),
        )) {
            val dominant = luma(palette, slot = 0)
            val brightest = (0 until PALETTE_SIZE).maxOf { luma(palette, it) }
            assertTrue(brightest < dominant * 2.2f, "vurgu baskın rengi gölgede bırakıyor")
        }
    }

    @Test
    fun grey_photo_stays_neutral() {
        // Renksiz görselden uydurma ton üretmemeli: eskiden buradan parlak macenta çıkıyordu.
        val palette = extractPalette(photo(0xFF555555.toInt(), 0xFF777777.toInt(), 0.90f))

        for (slot in 0 until PALETTE_SIZE) {
            assertTrue(channelSpread(palette, slot) < 0.08f, "yuva $slot renk uydurmuş")
        }
    }

    @Test
    fun outlines_do_not_take_over() {
        // Yarısı siyah kontur: kontur baskın olsaydı bütün yuvalar gri ve aynı olurdu.
        val pixels = IntArray(600) { index ->
            when {
                index < 300 -> 0xFF000000.toInt()
                index < 450 -> 0xFF1E5AC8.toInt()
                else -> 0xFFD8365A.toInt()
            }
        }

        val palette = extractPalette(pixels)

        assertTrue(channelSpread(palette, slot = 0) > 0.15f, "baskın renk çamurlu")
        assertTrue(distance(palette, palette, 0, 1) > 0.1f, "ilk iki bölge ayrışmalı")
    }

    private fun luma(palette: FloatArray, slot: Int): Float =
        0.2126f * palette[slot * 3] + 0.7152f * palette[slot * 3 + 1] + 0.0722f * palette[slot * 3 + 2]

    private fun meanLuma(palette: FloatArray): Float =
        (0 until PALETTE_SIZE).map { luma(palette, it) }.average().toFloat()

    private fun meanSpread(palette: FloatArray): Float =
        (0 until PALETTE_SIZE).map { channelSpread(palette, it) }.average().toFloat()

    private fun channelSpread(palette: FloatArray, slot: Int): Float {
        val rgb = palette.copyOfRange(slot * 3, slot * 3 + 3)
        return rgb.max() - rgb.min()
    }

    private fun distance(a: FloatArray, b: FloatArray, slot: Int): Float =
        distance(a, b, slot, slot)

    private fun distance(a: FloatArray, b: FloatArray, slotA: Int, slotB: Int): Float =
        (0 until 3).sumOf { abs(a[slotA * 3 + it] - b[slotB * 3 + it]).toDouble() }.toFloat()
}
