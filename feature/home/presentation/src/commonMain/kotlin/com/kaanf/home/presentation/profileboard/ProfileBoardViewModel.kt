package com.kaanf.home.presentation.profileboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.designsystem.profileboard.ProfileBoardAction
import com.kaanf.core.designsystem.profileboard.ProfileBoardEditor
import com.kaanf.core.designsystem.profileboard.ProfileBoardState
import com.kaanf.core.domain.model.profileboard.ProfileBoard
import com.kaanf.core.domain.repository.ProfileBoardRepository
import com.kaanf.core.domain.repository.UserRepository
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.util.UIText
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.profile_board_notification_title
import crew.feature.home.presentation.generated.resources.profile_board_load_error
import crew.feature.home.presentation.generated.resources.profile_board_save_error
import crew.feature.home.presentation.generated.resources.profile_board_conflict
import crew.feature.home.presentation.generated.resources.profile_board_unsupported
import crew.feature.home.presentation.generated.resources.profile_board_qr_image_error
import org.jetbrains.compose.resources.StringResource
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileBoardViewModel(
    private val userRepository: UserRepository,
    private val boardRepository: ProfileBoardRepository,
    private val snackbarController: SnackbarController,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileBoardState())
    val state = _state.asStateFlow()
    private var savedBoard = ProfileBoard()
    private var editingBaseline: ProfileBoardState? = null
    private var draftRevision = 0L
    private var isRefreshing = false
    private var hasLoadedBoard = false
    private val recoveredQrImages = mutableSetOf<String>()

    init {
        userRepository.observeCurrentUser().onEach { user ->
            _state.update { it.copy(
                publicCode = user?.publicCode.orEmpty(),
                fullName = user?.fullName.orEmpty(),
                profilePictureUrl = user?.profilePictureUrl,
            ) }
        }.launchIn(viewModelScope)
        boardRepository.observeBoard().onEach { board ->
            if (board != null) {
                savedBoard = board
                // A remote refresh must never silently replace an active editing draft.
                if (!_state.value.isEditingBoard && !_state.value.isSaving) applyBoard(board)
            }
        }.launchIn(viewModelScope)
        boardRepository.observeQrRender().onEach { render ->
            _state.update { it.copy(qrRender = render) }
        }.launchIn(viewModelScope)
        state.map { !it.isLoading && !it.canEdit && !it.hasLoadError }
            .distinctUntilChanged().onEach { unsupported ->
                if (unsupported) notify(Res.string.profile_board_unsupported, SnackbarVariant.Warn)
            }.launchIn(viewModelScope)
    }

    fun onAction(action: ProfileBoardAction) {
        if (_state.value.isSaving) return
        when (action) {
            is ProfileBoardAction.LayoutChanged -> _state.update { ProfileBoardEditor.reduce(it, action) }
            ProfileBoardAction.FinishEditing -> save()
            ProfileBoardAction.CancelEditing -> {
                _state.update { it.copy(isEditingBoard = false, pickerSlot = null, focusedStickerSlot = null,
                    hasSaveError = false, hasConflict = false, isEditorSheetOpen = false) }
                applyBoard(savedBoard)
            }
            ProfileBoardAction.ReloadBoard -> {
                onAction(ProfileBoardAction.CancelEditing)
                refresh()
            }
            else -> {
                if (!_state.value.canEdit || _state.value.isLoading || _state.value.hasLoadError || _state.value.hasConflict) return
                if (!_state.value.isEditingBoard &&
                    (action == ProfileBoardAction.EditBoard || action is ProfileBoardAction.OpenStickerPicker)) {
                    draftRevision = savedBoard.revision
                    editingBaseline = ProfileBoardEditor.settleBoard(_state.value)
                }
                _state.update { ProfileBoardEditor.reduce(it, action).copy(hasSaveError = false) }
            }
        }
    }

    private fun applyBoard(board: ProfileBoard) {
        _state.update { it.copy(stickers = board.stickers, qrStyleId = board.qrStyleId, canEdit = board.canEdit,
            qr = board.qr, boardLayout = it.boardLayout.copy(qr = board.qr.bounds())) }
    }

    private fun save() {
        val current = ProfileBoardEditor.settleBoard(_state.value)
        if (!current.isEditingBoard || current.hasConflict || !current.canEdit) return
        val baseline = editingBaseline?.let {
            ProfileBoardEditor.settleBoard(it.copy(boardLayout = current.boardLayout))
        }
        if (baseline != null && ProfileBoardEditor.hasSameContent(current, baseline)) {
            onAction(ProfileBoardAction.CancelEditing)
            return
        }
        val draft = ProfileBoard(revision = draftRevision, qrStyleId = current.qrStyleId, qr = current.qr, stickers = current.stickers.map {
            com.kaanf.core.domain.model.profileboard.BoardStickerPlacement.constrain(it, current.boardLayout)
        })
        _state.update { it.copy(isSaving = true, hasSaveError = false, pickerSlot = null, isEditorSheetOpen = false, stickers = draft.stickers, qr = draft.qr, boardLayout = current.boardLayout) }
        viewModelScope.launch {
            when (val result = boardRepository.save(draft)) {
                is Result.Success -> {
                    savedBoard = result.data
                    _state.update { it.copy(isSaving = false, isEditingBoard = false,
                        focusedStickerSlot = null, hasSaveError = false, hasConflict = false, isEditorSheetOpen = false) }
                    applyBoard(result.data)
                }
                is Result.Failure -> {
                    val conflict = (result.error as? DataError.Remote.Business)?.code == "PROFILE_BOARD_CONFLICT"
                    _state.update { it.copy(isSaving = false, hasSaveError = !conflict, hasConflict = conflict) }
                    notify(if (conflict) Res.string.profile_board_conflict else Res.string.profile_board_save_error)
                }
            }
        }
    }

    fun refresh() {
        if (isRefreshing || _state.value.isEditingBoard || _state.value.isSaving) return
        isRefreshing = true
        _state.update { it.copy(isLoading = !hasLoadedBoard, hasLoadError = false) }
        viewModelScope.launch {
            val user = userRepository.getUser()
            val board = boardRepository.refresh()
            if (board is Result.Success) {
                savedBoard = board.data
                hasLoadedBoard = true
                applyBoard(board.data)
            }
            _state.update { it.copy(isLoading = false, hasLoadError = user is Result.Failure || board is Result.Failure) }
            isRefreshing = false
            if (_state.value.hasLoadError) notify(Res.string.profile_board_load_error)
        }
    }

    fun onQrImageError() {
        val failedUrl = _state.value.qrRender?.imageUrl
        viewModelScope.launch {
            // One server-side recovery per image: a re-render replaces the URL and the board reloads it.
            if (failedUrl != null && recoveredQrImages.add(failedUrl)) {
                val result = boardRepository.recoverQrRender()
                if (result is Result.Success && result.data.imageUrl != failedUrl) return@launch
            }
            notify(Res.string.profile_board_qr_image_error)
        }
    }

    private suspend fun notify(description: StringResource, variant: SnackbarVariant = SnackbarVariant.Error) {
        snackbarController.show(SnackbarMessage(
            title = UIText.Resource(Res.string.profile_board_notification_title),
            description = UIText.Resource(description),
            variant = variant,
        ))
    }
}
