package com.kaanf.core.domain.model.profileboard

import kotlin.random.Random
import kotlin.test.*

class BoardQrPlacementTest {
    @Test
    fun panAndZoomCannotPushAnyQrEdgeOutsideTheCanvas() {
        val random = Random(57)
        var qr = BoardQrPlacement()
        repeat(5_000) {
            qr = qr.transform(random.nextFloat() * 4f - 2f, random.nextFloat() * 4f - 2f, .3f + random.nextFloat() * 3)
            val bounds = qr.bounds()
            assertTrue(bounds.left >= -.00001f && bounds.right <= 1.00001f)
            assertTrue(bounds.top >= -.00001f && bounds.bottom <= BoardStickerPlacement.HEIGHT + .00001f)
            assertTrue(qr.size in BoardQrPlacement.MIN_SIZE..BoardQrPlacement.MAX_SIZE)
            assertTrue(bounds.top >= BoardIdentityLayout.QR_TOP - .00001f)
        }
    }

    @Test
    fun themeMinimumHoldsAndNeverExceedsTheRoomBelowTheIdentity() {
        val vampire = minQrSize(BoardQrTheme.VAMPIRE.id)
        assertEquals(vampire, BoardQrPlacement(size = .3f).transform(0f, 0f, .1f, minSize = vampire).size)
        assertEquals(.3f, BoardQrPlacement(size = .3f).transform(0f, 0f, 1f, minTop = 1.7f, minSize = vampire).size, .0001f)
        assertEquals(BoardQrPlacement.MIN_SIZE, minQrSize("rounded"))
    }

    @Test
    fun invalidInputDoesNotMoveQr() {
        val qr = BoardQrPlacement()
        assertEquals(qr, qr.transform(Float.NaN, 0f, 1f))
        assertEquals(qr, qr.transform(0f, Float.POSITIVE_INFINITY, 1f))
        assertEquals(qr, qr.transform(0f, 0f, -1f))
    }
    @Test
    fun defaultQrStartsBelowIdentityOnTheExpandedBoard() {
        assertEquals(1.12f, BoardQrPlacement().y)
        assertTrue(BoardStickerPlacement.HEIGHT > 1.75f)
    }

    @Test
    fun qrCannotMoveOrGrowOverProfileIdentity() {
        for (zoom in listOf(.5f, 1f, 10f)) {
            val qr = BoardQrPlacement().transform(0f, -100f, zoom)
            assertTrue(qr.bounds().top >= BoardIdentityLayout.QR_TOP - .00001f)
        }
    }

    @Test
    fun measuredLongNameCanReserveMoreSpaceWithoutQrOverlap() {
        val qr = BoardQrPlacement().transform(0f, -100f, 10f, minTop = 1.3f)
        assertTrue(qr.bounds().top >= 1.3f - .00001f)
        assertTrue(qr.bounds().bottom <= BoardStickerPlacement.HEIGHT + .00001f)
    }

    @Test
    fun qrCanReachTheActualMeasuredStartOfDottedArea() {
        val qr = BoardQrPlacement(size = .3f).transform(0f, -100f, 1f, minTop = .5f)
        assertEquals(.5f, qr.bounds().top, .00001f)
    }

}
