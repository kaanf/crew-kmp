package com.kaanf.core.palette

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Reference drawing parameters, checked with the captured Luma palettes as fixtures. */
class CoverBackdropGeometryTest {

    private val pink = fixture(0xFFEDACCA, 0xFF312724, 0xFFFFFFFF, 0xFFCF5B72)
    private val light = fixture(0xFFFEFEFE, 0xFF819879, 0xFF070707, 0xFF1E98D4)

    @Test
    fun baseGradientFollowsHsvFormula() {
        // #EDACCA: h ≈ 332.3°, s ≈ 0.274, v ≈ 0.929 → clamped to 0.85
        val (top, bottom) = pink.baseGradient()
        assertRgb(0xD99DB9, top)
        val bottomHsv = bottom.toArgb().toHsv()
        assertEquals(min(1f, 0.274f / 2f + 0.75f), bottomHsv.saturation, 0.01f)
        assertEquals(0.85f * 0.75f, bottomHsv.value, 0.005f)
        assertEquals(332.3f, bottomHsv.hue, 1f)
    }

    @Test
    fun neutralBaseStaysNeutralAndValueIsClamped() {
        val (top, bottom) = light.baseGradient()
        assertRgb(0xD9D9D9, top) // v 0.996 → 0.85
        assertRgb(0xA3A3A3, bottom) // saturation stays 0, v × 0.75
        val (darkTop, _) = fixture(0xFF050505).baseGradient()
        assertEquals(0.2f, darkTop.toArgb().toHsv().value, 0.005f) // v 0.02 → 0.2
    }

    @Test
    fun circlesUseReferenceGeometryAndDrawOrder() {
        val circles = pink.circles(Size(400f, 800f), pxPerDp = 2f)
        assertEquals(3, circles.size)
        val (topEnd, middle, bottom) = circles

        assertEquals(Color(0xFFCF5B72).copy(alpha = 0.5f), topEnd.color) // colors[3], drawn first
        assertOffset(Offset(400f - 140f + 140f / 3f, 140f - 140f / 3f), topEnd.center)
        assertEquals(140f, topEnd.radius)
        assertEquals(180f, topEnd.blurRadius) // 90dp at 2x

        assertEquals(Color(0xFFFFFFFF).copy(alpha = 0.5f), middle.color) // colors[2]
        assertOffset(Offset(200f - 400f / 3f, 400f - 40f), middle.center)
        assertEquals(200f, middle.radius)
        assertEquals(240f, middle.blurRadius) // 120dp

        assertEquals(Color(0xFF312724).copy(alpha = 0.5f), bottom.color) // colors[1], drawn last
        assertOffset(Offset(100f, 780f), bottom.center)
        assertEquals(300f, bottom.radius)
        assertEquals(300f, bottom.blurRadius) // 150dp
    }

    @Test
    fun density15MatchesCapturedBlurRadii() {
        val blur = pink.circles(Size(1080f, 2400f), pxPerDp = 1.5f).map { it.blurRadius }
        assertEquals(listOf(135f, 180f, 225f), blur)
    }

    @Test
    fun snapshotScaleScalesEveryParameterTogether() {
        val full = pink.circles(Size(400f, 800f), pxPerDp = 2f)
        val quarter = pink.circles(Size(100f, 200f), pxPerDp = 2f * 0.25f)
        full.zip(quarter).forEach { (f, q) ->
            assertOffset(f.center * 0.25f, q.center)
            assertEquals(f.radius * 0.25f, q.radius, 1e-3f)
            assertEquals(f.blurRadius * 0.25f, q.blurRadius, 1e-3f)
        }
    }

    @Test
    fun missingRolesAreNotDrawn() {
        val circles = BackdropPalette(base = pink.base, bottom = pink.bottom).circles(Size(400f, 800f), 1f)
        assertEquals(listOf(Color(0xFF312724).copy(alpha = 0.5f)), circles.map { it.color })
    }

    @Test
    fun roleOrderMapsToCirclesAndEmptyMeansNoBackdrop() {
        assertEquals(null, BackdropPalette.fromRoleOrder(emptyList()))
        assertEquals(BackdropPalette(base = 1, bottom = 2), BackdropPalette.fromRoleOrder(listOf(1, 2)))
        assertEquals(BackdropPalette(1, 2, 3, 4), BackdropPalette.fromRoleOrder(listOf(1, 2, 3, 4, 5)))
    }

    @Test
    fun overlayStartsAtReferenceAlphaAndRisesOnlyForContrast() {
        val background = Color(0xFF0E0B08)
        val text = Color(0xFFC8BFB1)
        val dark = fixture(0xFF312724, 0xFF1A1A1A)
        assertEquals(REFERENCE_OVERLAY_ALPHA, dark.overlayAlpha(background, text))

        val alpha = light.overlayAlpha(background, text)
        assertTrue(alpha > REFERENCE_OVERLAY_ALPHA && alpha <= 0.9f, "alpha=$alpha")
        val (top, _) = light.baseGradient()
        val composited = blend(top.toArgb(), background.toArgb(), alpha)
        assertTrue(contrastRatio(text.toArgb(), composited) >= MIN_TEXT_CONTRAST)
    }

    private fun fixture(vararg colors: Long): BackdropPalette =
        BackdropPalette.fromRoleOrder(colors.map { it.toInt() })!!

    private fun assertRgb(expected: Int, actual: Color) {
        val argb = actual.toArgb()
        for (shift in listOf(16, 8, 0)) {
            val e = (expected shr shift) and 0xFF
            val a = (argb shr shift) and 0xFF
            assertTrue(abs(e - a) <= 1, "expected #${expected.toString(16)} got #${(argb and 0xFFFFFF).toString(16)}")
        }
    }

    private fun assertOffset(expected: Offset, actual: Offset) {
        assertEquals(expected.x, actual.x, 1e-3f)
        assertEquals(expected.y, actual.y, 1e-3f)
    }

    private fun min(a: Float, b: Float) = kotlin.math.min(a, b)
}
