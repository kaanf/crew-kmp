package com.kaanf.core.designsystem.profileboard

import com.kaanf.core.domain.model.movie.Movie

import com.kaanf.core.domain.model.music.MusicTrack
import com.kaanf.core.domain.model.profileboard.BoardLayout
import com.kaanf.core.domain.model.profileboard.BoardStickerDesign

sealed interface ProfileBoardAction {
    data object FocusQr : ProfileBoardAction
    data class TransformQr(val dx: Float, val dy: Float, val zoom: Float, val isGesture: Boolean = false) : ProfileBoardAction
    data object FinishQrGesture : ProfileBoardAction
    data class FocusSticker(val slot: Int) : ProfileBoardAction
    data class OpenStickerPicker(val slot: Int) : ProfileBoardAction
    data object DismissStickerPicker : ProfileBoardAction
    data class SelectSticker(val design: BoardStickerDesign) : ProfileBoardAction
    data object RemoveSticker : ProfileBoardAction
    data class MoveSticker(val slot: Int, val dx: Float, val dy: Float) : ProfileBoardAction
    data class TransformSticker(val slot: Int, val dx: Float, val dy: Float, val zoom: Float, val rotation: Float, val isGesture: Boolean = false) : ProfileBoardAction
    data class FinishStickerGesture(val slot: Int) : ProfileBoardAction
    data class DeleteSticker(val slot: Int) : ProfileBoardAction
    data object FinishEditing : ProfileBoardAction
    data class LayoutChanged(val layout: BoardLayout) : ProfileBoardAction
    data class SelectQrStyle(val id: String) : ProfileBoardAction
    data object CancelEditing : ProfileBoardAction
    data object ReloadBoard : ProfileBoardAction
    data class SelectEditorCategory(val category: BoardEditorCategory) : ProfileBoardAction
    data object OpenBoardEditor : ProfileBoardAction
    data object EditBoard : ProfileBoardAction
    data class OpenBoardTool(val tool: BoardTool) : ProfileBoardAction
    data object CloseBoardTool : ProfileBoardAction
    data class SearchMovies(val query: String) : ProfileBoardAction
    data object LoadMoreMovies : ProfileBoardAction
    data class ApplyMovie(val movie: Movie) : ProfileBoardAction
    data object RemoveMovie : ProfileBoardAction
    data class SearchSongs(val query: String) : ProfileBoardAction
    data object LoadMoreSongs : ProfileBoardAction
    data class ApplySong(val track: MusicTrack) : ProfileBoardAction
    data object RemoveSong : ProfileBoardAction
    data class ApplyAskMe(val text: String) : ProfileBoardAction
    data object RemoveAskMe : ProfileBoardAction
    data class ApplyHobbies(val hobbies: List<String>) : ProfileBoardAction
    data class RemoveHobby(val hobby: String) : ProfileBoardAction
}
