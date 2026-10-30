package com.kaanf.core.presentation.util.mediapicker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DecodeSampleSizeTest {
    @Test
    fun panoramasAreSampledInEitherOrientation() {
        assertEquals(4, calculateDecodeSampleSize(12_000, 2_000, 2560))
        assertEquals(4, calculateDecodeSampleSize(2_000, 12_000, 2560))
        assertEquals(4, calculateDecodeSampleSize(12_000, 2_000, 1600))
    }

    @Test
    fun smallAndBoundaryImagesKeepTheirResolution() {
        assertEquals(1, calculateDecodeSampleSize(100, 200, 2560))
        assertEquals(1, calculateDecodeSampleSize(5119, 2000, 2560))
        assertEquals(2, calculateDecodeSampleSize(5120, 2000, 2560))
    }

    @Test
    fun squarePortraitAndExtremeInputsPreserveCropResolutionWithoutOverflow() {
        for ((width, height) in listOf(8000 to 8000, 4000 to 8000, Int.MAX_VALUE to 1)) {
            val target = 2560
            val sample = calculateDecodeSampleSize(width, height, target)
            val decodedLongest = maxOf(width, height) / sample
            assertTrue(sample > 0 && sample.countOneBits() == 1)
            assertTrue(decodedLongest >= target)
            assertTrue(decodedLongest < target * 2)
        }
    }

    @Test
    fun invalidDimensionsAreRejected() {
        assertFailsWith<IllegalArgumentException> { calculateDecodeSampleSize(100, 100, 0) }
        assertFailsWith<IllegalArgumentException> { calculateDecodeSampleSize(0, 100, 2560) }
    }
}
