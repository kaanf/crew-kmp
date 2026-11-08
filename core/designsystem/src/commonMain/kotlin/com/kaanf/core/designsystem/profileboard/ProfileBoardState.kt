package com.kaanf.core.designsystem.profileboard

import com.kaanf.core.domain.model.movie.Movie

import com.kaanf.core.domain.model.profileboard.BoardLayout
import com.kaanf.core.domain.model.music.MusicTrack
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
    /** The tile tool sheet open over the board, if any. */
    val openTool: BoardTool? = null,
    /** Saved immediately through its own endpoint, independent of the board revision. */
    val favoriteTrack: MusicTrack? = null,
    val favoriteMovie: Movie? = null,
    val movieSearch: MovieSearchState = MovieSearchState(),
    val songSearch: SongSearchState = SongSearchState(),
    // Ask-me remains screen-local; hobbies are saved with the board revision.
    val askMe: String? = null,
    val hobbies: List<String> = emptyList(),
) {
    val hasTileContent: Boolean get() = favoriteTrack != null || favoriteMovie != null || askMe != null || hobbies.isNotEmpty()

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

data class SongSearchState(
    val query: String = "",
    val tracks: List<MusicTrack> = emptyList(),
    val nextOffset: Int? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val isSaving: Boolean = false,
)

object BoardTileLimits {
    const val ASK_ME_LENGTH = 60
    const val HOBBY_LENGTH = 24
    const val MAX_HOBBIES = 5
}

data class MovieSearchState(
    val query: String = "",
    val movies: List<Movie> = emptyList(),
    val nextPage: Int? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val isSaving: Boolean = false,
)
