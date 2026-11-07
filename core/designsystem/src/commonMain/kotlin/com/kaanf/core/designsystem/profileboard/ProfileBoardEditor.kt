package com.kaanf.core.designsystem.profileboard

import com.kaanf.core.domain.model.profileboard.BoardStickerPlacement
import com.kaanf.core.domain.model.profileboard.isKnownQrDesign
import com.kaanf.core.domain.model.profileboard.minQrSize

/** All edit permissions live here as well as in the gesture UI, including stale pointer events. */
object ProfileBoardEditor {
    /** Only persisted artwork matters; focus, panel state and sticker list order do not. */
    fun hasSameContent(first: ProfileBoardState, second: ProfileBoardState): Boolean =
        first.qrStyleId == second.qrStyleId && first.qr == second.qr &&
            first.stickers.sortedBy { it.slot } == second.stickers.sortedBy { it.slot }

    fun reduce(current: ProfileBoardState, action: ProfileBoardAction): ProfileBoardState = when (action) {
        ProfileBoardAction.FocusQr -> if (canEditQr(current)) current.copy(isQrFocused = true, focusedStickerSlot = null, pickerSlot = null) else current
        is ProfileBoardAction.TransformQr -> if (canEditQr(current)) {
            val qr = current.qr.transform(action.dx, action.dy, action.zoom, current.boardLayout.contentTop, minQrSize(current.qrStyleId), current.boardLayout.height)
            val preview = current.copy(qr = qr, boardLayout = current.boardLayout.copy(qr = qr.bounds()), isQrFocused = true, focusedStickerSlot = null, pickerSlot = null)
            if (action.isGesture) preview else settleBoard(preview)
        } else current
        ProfileBoardAction.FinishQrGesture -> if (canEditQr(current)) settleBoard(current) else current
        ProfileBoardAction.CancelEditing, ProfileBoardAction.ReloadBoard -> current.copy(
            isEditingBoard = false, focusedStickerSlot = null, pickerSlot = null, isEditorSheetOpen = false, isQrFocused = false,
        )
        is ProfileBoardAction.SelectEditorCategory -> if (current.isEditingBoard && current.isEditorSheetOpen) {
            current.copy(editorCategory = action.category)
        } else current
        is ProfileBoardAction.SelectQrStyle -> if (current.isEditingBoard && current.isEditorSheetOpen && isKnownQrDesign(action.id)) {
            settleBoard(current.copy(qrStyleId = action.id))
                .copy(isQrFocused = true, focusedStickerSlot = null, pickerSlot = null)
        } else current
        is ProfileBoardAction.FocusSticker -> {
            if (current.isEditingBoard && current.stickers.any { it.slot == action.slot }) {
                current.copy(focusedStickerSlot = action.slot, isQrFocused = false,
                    pickerSlot = if (current.isEditorSheetOpen) action.slot else null)
            } else current
        }
        is ProfileBoardAction.OpenStickerPicker -> {
            val existing = current.stickers.any { it.slot == action.slot }
            if (action.slot in 0..3 && (current.isEditingBoard || !existing)) {
                settleBoard(current).copy(
                    pickerSlot = action.slot,
                    isEditorSheetOpen = true,
                    editorCategory = if (current.editorCategory == BoardEditorCategory.QR) BoardEditorCategory.HOBBIES else current.editorCategory,
                    isEditingBoard = true,
                    focusedStickerSlot = if (existing) action.slot else current.focusedStickerSlot,
                )
            } else current
        }
        ProfileBoardAction.DismissStickerPicker -> current.copy(pickerSlot = null, isEditorSheetOpen = false)
        is ProfileBoardAction.SelectSticker -> selectSticker(current, action)
        ProfileBoardAction.RemoveSticker -> {
            if (current.isEditingBoard) deleteSticker(current, current.pickerSlot).copy(pickerSlot = null) else current
        }
        is ProfileBoardAction.MoveSticker -> {
            if (canTransform(current, action.slot)) current.copy(
                stickers = current.stickers.map {
                    if (it.slot == action.slot) BoardStickerPlacement.move(it, action.dx, action.dy, current.boardLayout) else it
                },
            ) else current
        }
        is ProfileBoardAction.TransformSticker -> {
            if (canTransform(current, action.slot)) current.copy(
                stickers = current.stickers.map {
                    if (it.slot != action.slot) it
                    else if (action.isGesture) BoardStickerPlacement.previewTransform(
                        it, action.dx, action.dy, action.zoom, action.rotation, current.boardLayout,
                    ) else BoardStickerPlacement.transform(
                        it, action.dx, action.dy, action.zoom, action.rotation, current.boardLayout,
                    )
                },
            ) else current
        }
        is ProfileBoardAction.FinishStickerGesture -> {
            if (canTransform(current, action.slot)) current.copy(stickers = current.stickers.map {
                if (it.slot == action.slot) BoardStickerPlacement.constrain(it, current.boardLayout) else it
            }) else current
        }
        is ProfileBoardAction.DeleteSticker -> {
            if (current.isEditingBoard) deleteSticker(current, action.slot) else current
        }
        ProfileBoardAction.FinishEditing -> settleBoard(current).copy(
            isEditingBoard = false, focusedStickerSlot = null, pickerSlot = null, isEditorSheetOpen = false, isQrFocused = false,
        )
        is ProfileBoardAction.LayoutChanged -> settleBoard(current.copy(boardLayout = action.layout))
        ProfileBoardAction.OpenBoardEditor -> if (current.isEditingBoard) settleBoard(current).copy(
            pickerSlot = null, isEditorSheetOpen = true,
        ) else current
        // Each tool's own sheet is added separately; opening one does not change the board yet.
        is ProfileBoardAction.OpenBoardTool -> current
        ProfileBoardAction.EditBoard -> settleBoard(current).copy(
            isEditingBoard = true,
            focusedStickerSlot = null,
            pickerSlot = null,
            isEditorSheetOpen = true,
            isQrFocused = false,
            editorCategory = BoardEditorCategory.QR,
        )
    }

    private fun canEditQr(state: ProfileBoardState) = state.isEditingBoard

    /** Keep saved placements scan-safe, including a resize interrupted by Save. */
    fun settleBoard(state: ProfileBoardState): ProfileBoardState {
        val minSize = minQrSize(state.qrStyleId)
        var qr = state.qr.transform(0f, 0f, 1f, state.boardLayout.contentTop, minSize, state.boardLayout.height)
        while (true) {
            val layout = state.boardLayout.copy(qr = qr.bounds())
            val stickers = state.stickers.map { BoardStickerPlacement.constrain(it, layout) }
            if (stickers.all { BoardStickerPlacement.isAllowed(it, layout) } || qr.size <= minSize) {
                return state.copy(qr = qr, boardLayout = layout, stickers = stickers)
            }
            qr = qr.transform(0f, 0f, ((qr.size - .02f) / qr.size).coerceAtLeast(.1f), state.boardLayout.contentTop, minSize, state.boardLayout.height)
        }
    }

    private fun canTransform(state: ProfileBoardState, slot: Int) =
        state.isEditingBoard && state.focusedStickerSlot == slot

    private fun selectSticker(state: ProfileBoardState, action: ProfileBoardAction.SelectSticker): ProfileBoardState {
        val slot = state.targetStickerSlot
        if (!state.isEditingBoard || (!state.isEditorSheetOpen && state.pickerSlot == null)) return state
        val existing = state.stickers.find { it.slot == slot }
        val replacement = BoardStickerPlacement.create(slot, action.design, state.boardLayout)
        val positioned = existing?.let {
            BoardStickerPlacement.constrain(
                replacement.copy(position = it.position, scale = it.scale, rotation = it.rotation), state.boardLayout,
            )
        } ?: replacement
        return state.copy(
            stickers = state.stickers.filterNot { it.slot == slot } + positioned,
            pickerSlot = null,
            isEditorSheetOpen = state.isEditorSheetOpen,
            focusedStickerSlot = slot,
            isQrFocused = false,
        )
    }

    private fun deleteSticker(state: ProfileBoardState, slot: Int?): ProfileBoardState = state.copy(
        stickers = state.stickers.filterNot { it.slot == slot },
        focusedStickerSlot = if (state.focusedStickerSlot == slot) null else state.focusedStickerSlot,
    )
}
