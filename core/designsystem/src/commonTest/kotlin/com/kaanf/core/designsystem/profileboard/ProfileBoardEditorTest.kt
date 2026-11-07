package com.kaanf.core.designsystem.profileboard

import com.kaanf.core.domain.model.profileboard.BoardPosition
import com.kaanf.core.domain.model.profileboard.BoardQrPlacement
import com.kaanf.core.domain.model.profileboard.BoardQrTheme
import com.kaanf.core.domain.model.profileboard.BoardStickerDesign
import com.kaanf.core.domain.model.profileboard.BoardStickerPlacement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProfileBoardEditorTest {
    @Test
    fun choosingAThemeGrowsTheQrToItsScanSafeMinimum() {
        val opened = ProfileBoardEditor.reduce(ProfileBoardState(isEditingBoard = true, isEditorSheetOpen = true,
            qr = BoardQrPlacement(size = BoardQrPlacement.MIN_SIZE)), ProfileBoardAction.SelectQrStyle(BoardQrTheme.VAMPIRE.id))
        assertEquals(BoardQrTheme.VAMPIRE.id, opened.qrStyleId)
        assertEquals(BoardQrTheme.VAMPIRE.minSize, opened.qr.size)
        val pinched = ProfileBoardEditor.reduce(opened, ProfileBoardAction.TransformQr(0f, 0f, .5f))
        assertEquals(BoardQrTheme.VAMPIRE.minSize, pinched.qr.size)
        val unknown = ProfileBoardEditor.reduce(opened, ProfileBoardAction.SelectQrStyle("future-style"))
        assertEquals(BoardQrTheme.VAMPIRE.id, unknown.qrStyleId)
    }

    @Test
    fun openingABoardToolKeepsTheBoardAndEditingStateUnchanged() {
        val editing = ProfileBoardEditor.reduce(ProfileBoardState(canEdit = true, isLoading = false), ProfileBoardAction.EditBoard)
        BoardTool.entries.forEach { tool ->
            assertEquals(editing, ProfileBoardEditor.reduce(editing, ProfileBoardAction.OpenBoardTool(tool)))
        }
    }

    private fun editingState(): ProfileBoardState = ProfileBoardState(
        isEditingBoard = true,
        focusedStickerSlot = 0,
        stickers = listOf(
            BoardStickerPlacement.create(0, BoardStickerDesign.STAR).copy(position = BoardPosition(0.3f, .25f)),
            BoardStickerPlacement.create(1, BoardStickerDesign.CREW).copy(position = BoardPosition(0.7f, .25f)),
        ),
    )

    @Test
    fun transformingFocusedStickerLeavesEveryOtherStickerUnchanged() {
        val before = editingState()
        val after = ProfileBoardEditor.reduce(before, ProfileBoardAction.TransformSticker(0, 0.03f, 0f, 1.2f, 20f))
        assertNotEquals(before.stickers[0], after.stickers[0])
        assertEquals(before.stickers[1], after.stickers[1])
    }

    @Test
    fun transformsForUnfocusedStickerAreIgnored() {
        val before = editingState()
        assertEquals(before, ProfileBoardEditor.reduce(before, ProfileBoardAction.TransformSticker(1, 0.1f, 0f, 1.2f, 20f)))
        assertEquals(before, ProfileBoardEditor.reduce(before, ProfileBoardAction.MoveSticker(1, 0.1f, 0f)))
    }

    @Test
    fun tappingAnotherStickerTransfersFocusWithoutChangingGeometry() {
        val before = editingState()
        val focused = ProfileBoardEditor.reduce(before, ProfileBoardAction.FocusSticker(1))
        assertEquals(1, focused.focusedStickerSlot)
        assertEquals(before.stickers, focused.stickers)
        val transformed = ProfileBoardEditor.reduce(focused, ProfileBoardAction.TransformSticker(1, 0f, 0f, 1.1f, 10f))
        assertEquals(before.stickers[0], transformed.stickers[0])
        assertNotEquals(before.stickers[1], transformed.stickers[1])
    }

    @Test
    fun doneLocksAllStickerMutationsIncludingStaleGestureEvents() {
        val locked = ProfileBoardEditor.reduce(editingState(), ProfileBoardAction.FinishEditing)
        assertNull(locked.focusedStickerSlot)
        assertNull(locked.pickerSlot)
        val lateActions = listOf(
            ProfileBoardAction.TransformSticker(0, 0.1f, 0.1f, 1.4f, 45f),
            ProfileBoardAction.MoveSticker(0, 0.1f, 0.1f),
            ProfileBoardAction.FocusSticker(0),
            ProfileBoardAction.DeleteSticker(0),
            ProfileBoardAction.RemoveSticker,
            ProfileBoardAction.SelectSticker(BoardStickerDesign.TROPHY),
            ProfileBoardAction.OpenStickerPicker(0),
        )
        lateActions.forEach { assertEquals(locked, ProfileBoardEditor.reduce(locked, it)) }
    }

    @Test
    fun editingCanBeExplicitlyReenabledAfterDone() {
        val locked = ProfileBoardEditor.reduce(editingState(), ProfileBoardAction.FinishEditing)
        val reopened = ProfileBoardEditor.reduce(locked, ProfileBoardAction.EditBoard)
        assertTrue(reopened.isEditingBoard)
        val sheetClosed = ProfileBoardEditor.reduce(reopened, ProfileBoardAction.FocusSticker(0))
        val moved = ProfileBoardEditor.reduce(sheetClosed, ProfileBoardAction.MoveSticker(0, 0.03f, 0f))
        assertNotEquals(locked.stickers[0], moved.stickers[0])
    }

    @Test
    fun paletteKeepsBoardTransformsAvailable() {
        val before = editingState().copy(pickerSlot = 0, isEditorSheetOpen = true)
        val transformed = ProfileBoardEditor.reduce(before, ProfileBoardAction.TransformSticker(0, 0f, 0f, 1.2f, 30f))
        assertNotEquals(before.stickers[0], transformed.stickers[0])
        assertTrue(transformed.isEditorSheetOpen)
    }

    @Test
    fun newStickerBecomesFocusedWithoutAffectingExistingOnes() {
        val before = editingState().copy(pickerSlot = 2)
        val after = ProfileBoardEditor.reduce(before, ProfileBoardAction.SelectSticker(BoardStickerDesign.SPARKLE))
        assertEquals(2, after.focusedStickerSlot)
        assertNull(after.pickerSlot)
        assertEquals(before.stickers, after.stickers.filterNot { it.slot == 2 })
    }

    @Test
    fun removingFocusedStickerClearsItsFocus() {
        val after = ProfileBoardEditor.reduce(editingState(), ProfileBoardAction.DeleteSticker(0))
        assertNull(after.focusedStickerSlot)
        assertEquals(listOf(1), after.stickers.map { it.slot })
    }
    @Test
    fun draggingCanCarryAStickerAcrossQrToTheOtherSide() {
        val start = editingState().copy(stickers = listOf(BoardStickerPlacement.create(0, BoardStickerDesign.CREW)))
        val targetY = 1.65f
        val dragged = ProfileBoardEditor.reduce(start, ProfileBoardAction.TransformSticker(
            0, 0f, targetY - start.stickers.first().position.y, 1f, 0f, isGesture = true,
        ))
        assertEquals(targetY, dragged.stickers.first().position.y, 0.00001f)
        val released = ProfileBoardEditor.reduce(dragged, ProfileBoardAction.FinishStickerGesture(0))
        assertTrue(BoardStickerPlacement.isAllowed(released.stickers.first()))
        assertEquals(dragged.stickers, released.stickers)
    }

    @Test
    fun releaseClearsQrEvenWhenTheFingerStopsOnTopOfIt() {
        val start = editingState()
        val dragged = ProfileBoardEditor.reduce(start, ProfileBoardAction.TransformSticker(
            0, .5f - start.stickers[0].position.x, start.qr.y - start.stickers[0].position.y, 1f, 0f, isGesture = true,
        ))
        assertTrue(!BoardStickerPlacement.isAllowed(dragged.stickers[0]))
        val released = ProfileBoardEditor.reduce(dragged, ProfileBoardAction.FinishStickerGesture(0))
        assertTrue(BoardStickerPlacement.isAllowed(released.stickers[0]))
        assertEquals(start.stickers[1], released.stickers[1])
    }

    @Test
    fun finishingEditingSettlesAnUnfinishedGestureAndRejectsLatePreviewEvents() {
        val start = editingState()
        val dragged = ProfileBoardEditor.reduce(start, ProfileBoardAction.TransformSticker(
            0, .2f, -.48f, 1.2f, 20f, isGesture = true,
        ))
        val done = ProfileBoardEditor.reduce(dragged, ProfileBoardAction.FinishEditing)
        assertTrue(done.stickers.all { BoardStickerPlacement.isAllowed(it) })
        assertEquals(done, ProfileBoardEditor.reduce(done, ProfileBoardAction.TransformSticker(
            0, .2f, .2f, 1f, 0f, isGesture = true,
        )))
    }

    @Test
    fun editBoardOpensCategorizedSheetAndChoosingQrKeepsTheDraftOpen() {
        val mode = ProfileBoardEditor.reduce(ProfileBoardState(), ProfileBoardAction.EditBoard)
        assertTrue(mode.isEditingBoard)
        assertTrue(mode.isEditorSheetOpen)
        val opened = ProfileBoardEditor.reduce(mode, ProfileBoardAction.OpenBoardEditor)
        assertTrue(opened.isEditorSheetOpen)
        assertEquals(BoardEditorCategory.QR, opened.editorCategory)
        val selected = ProfileBoardEditor.reduce(opened, ProfileBoardAction.SelectQrStyle("classic"))
        assertEquals("classic", selected.qrStyleId)
        assertTrue(selected.isEditorSheetOpen)
        val closed = ProfileBoardEditor.reduce(selected, ProfileBoardAction.DismissStickerPicker)
        assertTrue(closed.isEditingBoard)
        assertTrue(!closed.isEditorSheetOpen)
    }

    @Test
    fun categoryStickerSelectionUsesEmptySlotAndKeepsPaletteOpen() {
        val mode = ProfileBoardEditor.reduce(editingState(), ProfileBoardAction.EditBoard)
        val opened = ProfileBoardEditor.reduce(mode, ProfileBoardAction.OpenBoardEditor)
        val category = ProfileBoardEditor.reduce(opened, ProfileBoardAction.SelectEditorCategory(BoardEditorCategory.HOBBIES))
        val selected = ProfileBoardEditor.reduce(category, ProfileBoardAction.SelectSticker(BoardStickerDesign.HIKING))
        assertEquals(2, selected.focusedStickerSlot)
        assertEquals(BoardStickerDesign.HIKING, selected.stickers.last().design)
        assertTrue(selected.isEditorSheetOpen)
        assertTrue(selected.isEditingBoard)
    }

    @Test
    fun movingAndResizingQrKeepsQrInsideAndRepositionsStickersSafely() {
        val preview = ProfileBoardEditor.reduce(editingState(), ProfileBoardAction.TransformQr(20f, -20f, 2f, isGesture = true))
        assertTrue(preview.isQrFocused)
        assertNull(preview.focusedStickerSlot)
        val released = ProfileBoardEditor.reduce(preview, ProfileBoardAction.FinishQrGesture)
        val bounds = released.qr.bounds()
        assertTrue(bounds.left >= -.00001f && bounds.right <= 1.00001f && bounds.top >= released.boardLayout.contentTop - .00001f)
        assertTrue(released.stickers.all { BoardStickerPlacement.isAllowed(it, released.boardLayout) })
    }

    @Test
    fun qrTransformsWorkWithPaletteOpenAndStopAfterEditing() {
        val mode = ProfileBoardEditor.reduce(editingState(), ProfileBoardAction.EditBoard)
        val opened = ProfileBoardEditor.reduce(mode, ProfileBoardAction.OpenBoardEditor)
        val transformed = ProfileBoardEditor.reduce(opened, ProfileBoardAction.TransformQr(.2f, .1f, 1.2f))
        assertNotEquals(opened.qr, transformed.qr)
        assertTrue(transformed.isEditorSheetOpen)
        val done = ProfileBoardEditor.reduce(editingState(), ProfileBoardAction.FinishEditing)
        assertEquals(done, ProfileBoardEditor.reduce(done, ProfileBoardAction.TransformQr(.2f, .1f, 1.2f)))
    }

    @Test
    fun layoutMeasurementMovesOldStickersOutOfProfileBand() {
        val original = editingState()
        val layout = original.boardLayout.copy(contentTop = .75f)
        val settled = ProfileBoardEditor.reduce(original, ProfileBoardAction.LayoutChanged(layout))
        assertTrue(settled.stickers.all { BoardStickerPlacement.isAllowed(it, settled.boardLayout) })
        val slot = settled.focusedStickerSlot!!
        val moved = ProfileBoardEditor.reduce(settled, ProfileBoardAction.TransformSticker(
            slot, 0f, -100f, 1f, 0f, isGesture = true,
        ))
        val sticker = moved.stickers.first { it.slot == slot }
        val (_, height) = BoardStickerPlacement.footprint(sticker, moved.boardLayout)
        assertTrue(sticker.position.y - height / 2f >= layout.contentTop - .00001f)
    }

    @Test
    fun enteringEditingDoesNotPreselectAStickerOrQr() {
        val start = editingState().copy(isEditingBoard = false, isQrFocused = true)
        val editing = ProfileBoardEditor.reduce(start, ProfileBoardAction.EditBoard)
        assertNull(editing.focusedStickerSlot)
        assertTrue(!editing.isQrFocused)
        assertTrue(editing.isEditorSheetOpen)
    }

    @Test
    fun selectMoveAndRestyleWithoutClosingPalette() {
        var state = ProfileBoardEditor.reduce(editingState(), ProfileBoardAction.OpenBoardEditor)
        state = ProfileBoardEditor.reduce(state, ProfileBoardAction.SelectEditorCategory(BoardEditorCategory.HOBBIES))
        state = ProfileBoardEditor.reduce(state, ProfileBoardAction.SelectSticker(BoardStickerDesign.HIKING))
        val slot = state.focusedStickerSlot!!
        val before = state.stickers.first { it.slot == slot }
        state = ProfileBoardEditor.reduce(state, ProfileBoardAction.TransformSticker(slot, .03f, -.02f, 1f, 0f, isGesture = true))
        state = ProfileBoardEditor.reduce(state, ProfileBoardAction.FinishStickerGesture(slot))
        assertNotEquals(before.position, state.stickers.first { it.slot == slot }.position)
        assertTrue(state.isEditorSheetOpen)
        state = ProfileBoardEditor.reduce(state, ProfileBoardAction.SelectEditorCategory(BoardEditorCategory.QR))
        state = ProfileBoardEditor.reduce(state, ProfileBoardAction.SelectQrStyle("classic"))
        state = ProfileBoardEditor.reduce(state, ProfileBoardAction.TransformQr(.05f, .05f, .9f))
        assertEquals("classic", state.qrStyleId)
        assertTrue(state.isEditorSheetOpen)
        state = ProfileBoardEditor.reduce(state, ProfileBoardAction.FinishEditing)
        assertTrue(!state.isEditorSheetOpen)
        assertTrue(!state.isEditingBoard)
    }

    @Test
    fun bottomProfileBandBlocksQrAndStickersDuringGesturesAndAfterSave() {
        val bottom = 1.45f
        val original = editingState()
        val measured = ProfileBoardEditor.reduce(original, ProfileBoardAction.LayoutChanged(
            original.boardLayout.copy(contentTop = 0f, height = bottom),
        ))
        for (x in listOf(-10f, 0f, 10f)) {
            val qrMoved = ProfileBoardEditor.reduce(measured,
                ProfileBoardAction.TransformQr(x, 100f, 2f, isGesture = true))
            assertTrue(qrMoved.qr.bounds().bottom <= bottom + .00001f)
            val stickerMoved = ProfileBoardEditor.reduce(measured,
                ProfileBoardAction.TransformSticker(0, x, 100f, 1.6f, 45f, isGesture = true))
            val sticker = stickerMoved.stickers.first { it.slot == 0 }
            val (_, height) = BoardStickerPlacement.footprint(sticker, stickerMoved.boardLayout)
            assertTrue(sticker.position.y + height / 2f <= bottom + .00001f)
            val saved = ProfileBoardEditor.reduce(stickerMoved, ProfileBoardAction.FinishEditing)
            assertTrue(saved.qr.bounds().bottom <= bottom + .00001f)
            assertTrue(saved.stickers.all { BoardStickerPlacement.isAllowed(it, saved.boardLayout) })
        }
    }

    @Test
    fun longPressEntryOpensThePaletteAndFocusesThePressedObject() {
        val original = editingState().copy(isEditingBoard = false)
        val opened = ProfileBoardEditor.reduce(original, ProfileBoardAction.EditBoard)
        val qr = ProfileBoardEditor.reduce(opened, ProfileBoardAction.FocusQr)
        assertTrue(qr.isEditingBoard && qr.isEditorSheetOpen && qr.isQrFocused)
        assertNull(qr.focusedStickerSlot)
        val category = ProfileBoardEditor.reduce(opened, ProfileBoardAction.SelectEditorCategory(BoardEditorCategory.CREW))
        val sticker = ProfileBoardEditor.reduce(category, ProfileBoardAction.FocusSticker(0))
        assertTrue(sticker.isEditingBoard && sticker.isEditorSheetOpen)
        assertEquals(0, sticker.focusedStickerSlot)
        assertEquals(0, sticker.pickerSlot)
        assertEquals(BoardEditorCategory.CREW, sticker.editorCategory)
        assertEquals(opened.stickers, sticker.stickers)
    }

    @Test
    fun unchangedBoardIgnoresEditorUiAndStickerOrdering() {
        val original = editingState()
        val opened = ProfileBoardEditor.reduce(original, ProfileBoardAction.EditBoard)
        val focused = ProfileBoardEditor.reduce(opened, ProfileBoardAction.FocusSticker(1))
        assertTrue(ProfileBoardEditor.hasSameContent(opened, focused.copy(stickers = focused.stickers.reversed())))
    }

    @Test
    fun persistedChangesAreDetectedAndRevertingThemRestoresEquality() {
        val original = editingState()
        assertTrue(!ProfileBoardEditor.hasSameContent(original, original.copy(qrStyleId = "dots")))
        assertTrue(!ProfileBoardEditor.hasSameContent(original, original.copy(qr = original.qr.copy(x = .6f))))
        assertTrue(!ProfileBoardEditor.hasSameContent(original, original.copy(stickers = original.stickers.drop(1))))
        val moved = original.copy(stickers = original.stickers.map { it.copy(rotation = it.rotation + 5f) })
        assertTrue(!ProfileBoardEditor.hasSameContent(original, moved))
        assertTrue(ProfileBoardEditor.hasSameContent(original, moved.copy(stickers = original.stickers)))
    }

}
