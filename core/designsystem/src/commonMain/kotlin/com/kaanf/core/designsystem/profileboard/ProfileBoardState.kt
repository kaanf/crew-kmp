package com.kaanf.core.designsystem.profileboard

import com.kaanf.core.domain.model.profileboard.BoardLayout
import com.kaanf.core.domain.model.profileboard.BoardSticker

data class ProfileBoardState(
    val qrStyleId: String = "rounded",
    val qrRender: com.kaanf.core.domain.model.profileboard.BoardQrRender? = null,
    val isSaving: Boolean = false,
    val hasSaveError: Boolean = false,
    val hasConflict: Boolean = false,
    val canEdit: Boolean = false,
    val publicCode: String = "",
    val fullName: String = "",
    val profilePictureUrl: String? = null,
    val isLoading: Boolean = true,
    val hasLoadError: Boolean = false,
    val qr: com.kaanf.core.domain.model.profileboard.BoardQrPlacement = com.kaanf.core.domain.model.profileboard.BoardQrPlacement(),
    val isQrFocused: Boolean = false,
    val boardLayout: BoardLayout = BoardLayout(qr = qr.bounds()),
    val stickers: List<BoardSticker> = emptyList(),
    val isEditingBoard: Boolean = false,
    val focusedStickerSlot: Int? = null,
    val pickerSlot: Int? = null,
    val isEditorSheetOpen: Boolean = false,
    val editorCategory: BoardEditorCategory = BoardEditorCategory.QR,
) {
    val targetStickerSlot: Int get() = pickerSlot
        ?: (0..3).firstOrNull { slot -> stickers.none { it.slot == slot } }
        ?: focusedStickerSlot ?: 0
}

enum class BoardEditorCategory(val stickerCategory: com.kaanf.core.domain.model.profileboard.BoardStickerCategory? = null) {
    QR,
    HOBBIES(com.kaanf.core.domain.model.profileboard.BoardStickerCategory.HOBBIES),
    MUSIC(com.kaanf.core.domain.model.profileboard.BoardStickerCategory.MUSIC),
    SPORTS(com.kaanf.core.domain.model.profileboard.BoardStickerCategory.SPORTS),
    SOCIAL(com.kaanf.core.domain.model.profileboard.BoardStickerCategory.SOCIAL),
    CREW(com.kaanf.core.domain.model.profileboard.BoardStickerCategory.CREW),
}

/** Tools offered by the edit board dock, in display order. */
enum class BoardTool { QR_SKIN, FILM, SONG, ASK, LOVES }
