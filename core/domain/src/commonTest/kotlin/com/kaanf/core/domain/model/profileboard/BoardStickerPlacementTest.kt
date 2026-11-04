package com.kaanf.core.domain.model.profileboard

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoardStickerPlacementTest {
    @Test
    fun everyDesignStartsInsideBoardAndOutsideQrInEverySlot() {
        for (slot in 0..3) {
            for (design in BoardStickerDesign.entries) {
                assertTrue(BoardStickerPlacement.isAllowed(BoardStickerPlacement.create(slot, design)))
            }
        }
    }

    @Test
    fun largeDragCannotJumpFromAboveQrToBelowIt() {
        val sticker = BoardStickerPlacement.create(0, BoardStickerDesign.STAR)
        val moved = BoardStickerPlacement.move(sticker, 0.26f, 100f)
        assertTrue(BoardStickerPlacement.isAllowed(moved))
        assertTrue(moved.position.y < BoardLayout().qr.top)
        assertTrue(moved.position.y > sticker.position.y)
    }

    @Test
    fun largeDragCannotJumpFromBelowQrToAboveIt() {
        val sticker = BoardStickerPlacement.create(2, BoardStickerDesign.CREW)
        val moved = BoardStickerPlacement.move(sticker, 0.36f, -100f)
        assertTrue(BoardStickerPlacement.isAllowed(moved))
        assertTrue(moved.position.y > BoardLayout().qr.bottom)
    }

    @Test
    fun stickerSlidesHorizontallyWhenItTouchesQr() {
        val sticker = BoardStickerPlacement.create(0, BoardStickerDesign.STAR)
        val againstQr = BoardStickerPlacement.move(sticker, 0.26f, 1f)
        val slid = BoardStickerPlacement.move(againstQr, 0.15f, 0.15f)
        assertTrue(slid.position.x > againstQr.position.x)
        assertEquals(againstQr.position.y, slid.position.y, 0.00001f)
        assertTrue(BoardStickerPlacement.isAllowed(slid))
    }

    @Test
    fun repeatedDragsKeepRotatedStickersInBoundsAndClearOfQr() {
        val random = Random(42)
        for (slot in 0..3) {
            for (design in BoardStickerDesign.entries) {
                var sticker = BoardStickerPlacement.create(slot, design)
                repeat(2_000) {
                    sticker = BoardStickerPlacement.move(
                        sticker,
                        random.nextFloat() * 4f - 2f,
                        random.nextFloat() * 4f - 2f,
                    )
                    assertTrue(BoardStickerPlacement.isAllowed(sticker), "$slot $design: $sticker")
                }
            }
        }
    }

    @Test
    fun sideDragsKeepQrClearForEveryDesign() {
        for (design in BoardStickerDesign.entries) {
            for (side in listOf(0f, 1f)) {
                var sticker = BoardStickerPlacement.create(0, design)
                sticker = BoardStickerPlacement.move(sticker, side - sticker.position.x, 0f)
                sticker = BoardStickerPlacement.move(sticker, 0f, 100f)
                assertTrue(BoardStickerPlacement.isAllowed(sticker), "$design at $side")
                val centered = BoardStickerPlacement.move(sticker, .5f - sticker.position.x, 0f)
                assertTrue(BoardStickerPlacement.isAllowed(centered))
            }
        }
    }

    @Test
    fun halfOfStickerCanExtendBeyondEitherScreenEdge() {
        val sticker = BoardStickerPlacement.create(0, BoardStickerDesign.CREW)
        val left = BoardStickerPlacement.move(sticker, -100f, 0f)
        val right = BoardStickerPlacement.move(sticker, 100f, 0f)
        assertEquals(0f, left.position.x)
        assertEquals(1f, right.position.x)
        assertTrue(BoardStickerPlacement.isAllowed(left))
        assertTrue(BoardStickerPlacement.isAllowed(right))
    }

    @Test
    fun stickersCannotEscapeCanonicalBoardVertically() {
        val layout = BoardLayout()
        val sticker = BoardStickerPlacement.create(0, BoardStickerDesign.STAR)
        val top = BoardStickerPlacement.move(sticker, 0f, -100f)
        assertTrue(top.position.y > 0f)
        val side = BoardStickerPlacement.move(top, -100f, 0f)
        val bottom = BoardStickerPlacement.move(side, 0f, 100f)
        assertTrue(bottom.position.y < layout.height)
        assertTrue(bottom.position.y > layout.qr.bottom)
        assertTrue(BoardStickerPlacement.isAllowed(bottom))
    }

    @Test
    fun changingQrBoundsRepositionsOverlappingStickers() {
        val sticker = BoardStickerPlacement.create(0, BoardStickerDesign.GOOD_VIBES)
        val resized = BoardLayout(qr = BoardRect(0.15f, 0.1f, 0.85f, 0.8f))
        val adjusted = BoardStickerPlacement.constrain(sticker, resized)
        assertTrue(BoardStickerPlacement.isAllowed(adjusted, resized))
    }

    @Test
    fun nonFiniteDragIsIgnored() {
        val sticker = BoardStickerPlacement.create(0, BoardStickerDesign.STAR)
        assertEquals(sticker, BoardStickerPlacement.move(sticker, Float.NaN, 0f))
        assertEquals(sticker, BoardStickerPlacement.move(sticker, 0f, Float.POSITIVE_INFINITY))
    }
    private val protectedLayout = BoardLayout(
        height = 2f,
        qr = BoardRect(.20f, .2952381f, .80f, .8952381f),
        protectedAreas = listOf(
            BoardRect(0.04f, 0.035f, 0.12f, 0.115f),
            BoardRect(0.68f, 0.035f, 0.96f, 0.115f),
            BoardRect(0.30f, 1.30f, 0.70f, 1.75f),
        ),
    )

    @Test
    fun pinchRespectsMinimumAndMaximumScale() {
        val sticker = BoardStickerPlacement.create(0, BoardStickerDesign.STAR)
            .copy(position = BoardPosition(0.5f, .35f))
        val large = BoardStickerPlacement.transform(sticker, 0f, 0f, 100f, 0f, BoardLayout(height = 2f))
        val small = BoardStickerPlacement.transform(large, 0f, 0f, 0.001f, 0f, BoardLayout(height = 2f))
        assertEquals(BoardStickerPlacement.MAX_SCALE, large.scale)
        assertEquals(BoardStickerPlacement.MIN_SCALE, small.scale)
    }

    @Test
    fun rotationIsAppliedAndNormalizedInFreeSpace() {
        val sticker = BoardStickerPlacement.create(0, BoardStickerDesign.CREW)
            .copy(position = BoardPosition(0.5f, .35f), rotation = 170f)
        val rotated = BoardStickerPlacement.transform(sticker, 0f, 0f, 1f, 30f, BoardLayout(height = 2f))
        assertEquals(-160f, rotated.rotation, 0.0001f)
        assertTrue(BoardStickerPlacement.isAllowed(rotated, BoardLayout(height = 2f)))
    }

    @Test
    fun resizingAndRotationStopBeforeEnteringQr() {
        val sticker = BoardStickerPlacement.create(0, BoardStickerDesign.CREW)
            .copy(position = BoardPosition(0.5f, BoardLayout().qr.top - 0.10f))
        assertTrue(BoardStickerPlacement.isAllowed(sticker))
        val transformed = BoardStickerPlacement.transform(sticker, 0f, 0f, 2f, 90f, BoardLayout())
        assertTrue(BoardStickerPlacement.isAllowed(transformed))
        assertTrue(transformed.rotation < 81f)
    }

    @Test
    fun draggingCannotCoverProfileIdentityOrButtons() {
        val sticker = BoardStickerPlacement.create(0, BoardStickerDesign.STAR, protectedLayout)
            .copy(position = BoardPosition(0.5f, 1.15f))
        val nearProfile = BoardStickerPlacement.move(sticker, 0f, 1f, protectedLayout)
        assertTrue(nearProfile.position.y < 1.30f)
        assertTrue(BoardStickerPlacement.isAllowed(nearProfile, protectedLayout))
        val right = BoardStickerPlacement.create(1, BoardStickerDesign.STAR, protectedLayout)
        val nearButton = BoardStickerPlacement.move(right, 0f, -100f, protectedLayout)
        assertTrue(nearButton.position.y > 0.115f)
        assertTrue(BoardStickerPlacement.isAllowed(nearButton, protectedLayout))
    }

    @Test
    fun everyDesignSurvivesRandomPanZoomRotationAroundAllProtectedContent() {
        val random = Random(109)
        for (design in BoardStickerDesign.entries) {
            var sticker = BoardStickerPlacement.create(0, design, protectedLayout)
            repeat(2_000) {
                sticker = BoardStickerPlacement.transform(
                    sticker,
                    random.nextFloat() * 2f - 1f,
                    random.nextFloat() * 2f - 1f,
                    0.5f + random.nextFloat(),
                    random.nextFloat() * 90f - 45f,
                    protectedLayout,
                )
                assertTrue(BoardStickerPlacement.isAllowed(sticker, protectedLayout), "$design: $sticker")
                assertTrue(sticker.scale in BoardStickerPlacement.MIN_SCALE..BoardStickerPlacement.MAX_SCALE)
            }
        }
    }

    @Test
    fun layoutChangeProtectsIdentityFromPreviouslyPlacedSticker() {
        val sticker = BoardStickerPlacement.create(2, BoardStickerDesign.GOOD_VIBES)
            .copy(position = BoardPosition(0.5f, 1.5f), scale = 1.6f, rotation = 45f)
        val adjusted = BoardStickerPlacement.constrain(sticker, protectedLayout)
        assertTrue(BoardStickerPlacement.isAllowed(adjusted, protectedLayout))
    }

    @Test
    fun widestStickerFitsBesideEitherSideOfQr() {
        for (x in listOf(.01f, .99f)) {
            val sticker = BoardSticker(0, BoardStickerDesign.PHOTOGRAPHY, BoardPosition(x, .6f), 0f)
            assertTrue(BoardStickerPlacement.isAllowed(sticker))
            assertEquals(sticker, BoardStickerPlacement.constrain(sticker, BoardLayout()))
        }
    }

    @Test
    fun profileBandRejectsOldPlacementsAndDragCannotEnterIt() {
        val layout = BoardLayout(contentTop = .7f)
        val old = BoardSticker(0, BoardStickerDesign.HIKING, BoardPosition(.5f, .2f), 25f)
        assertTrue(!BoardStickerPlacement.isAllowed(old, layout))
        val settled = BoardStickerPlacement.constrain(old, layout)
        assertTrue(BoardStickerPlacement.isAllowed(settled, layout))
        val preview = BoardStickerPlacement.previewTransform(settled, 0f, -100f, 1.4f, 20f, layout)
        val (_, height) = BoardStickerPlacement.footprint(preview, layout)
        assertTrue(preview.position.y - height / 2f >= layout.contentTop - .00001f)
    }

}
