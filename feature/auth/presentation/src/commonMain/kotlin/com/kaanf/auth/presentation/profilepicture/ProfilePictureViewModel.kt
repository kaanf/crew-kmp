package com.kaanf.auth.presentation.profilepicture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.repository.UserRepository
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfilePictureViewModel(
    private val userRepository: UserRepository,
    private val snackbarController: SnackbarController,
): ViewModel() {
    private val eventChannel = Channel<ProfilePictureEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(ProfilePictureState())
    val state =
        _state.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfilePictureState(),
        )

    fun onAction(action: ProfilePictureAction) {
        when (action) {
            is ProfilePictureAction.OnPictureSelected -> onPictureSelected(action.bytes)
            is ProfilePictureAction.OnReCropClick -> onReCrop()
            is ProfilePictureAction.OnRemoveClick -> onRemove()
            is ProfilePictureAction.OnCropConfirmed -> onCropConfirmed(action.bytes)
            is ProfilePictureAction.OnCropCancelled -> onCropCancelled()
            is ProfilePictureAction.OnConfirmClick -> uploadProfilePicture(
                bytes = state.value.selectedImageBytes,
                mimeType = state.value.selectedMimeType,
            )
            is ProfilePictureAction.OnSkipClick -> onSkip()
            else -> Unit
        }
    }

    private fun onRemove() {
        _state.update { it.copy(
            selectedImageBytes = null,
            selectedMimeType = null,
            originalImageBytes = null,
            pendingCropBytes = null,
        ) }
    }

    private fun onSkip() {
        viewModelScope.launch {
            eventChannel.send(ProfilePictureEvent.SkipSuccess)
        }
    }

    private fun onPictureSelected(bytes: ByteArray) {
        _state.update { it.copy(
            originalImageBytes = bytes,
            pendingCropBytes = bytes,
        ) }
    }

    private fun onReCrop() {
        // Re-open the cropper on the original full-resolution source, not the already-cropped result.
        val original = _state.value.originalImageBytes ?: return
        _state.update { it.copy(
            pendingCropBytes = original,
        ) }
    }

    private fun onCropConfirmed(bytes: ByteArray) {
        _state.update { it.copy(
            selectedImageBytes = bytes,
            selectedMimeType = "image/webp",
            pendingCropBytes = null,
        ) }
    }

    private fun onCropCancelled() {
        _state.update { it.copy(
            pendingCropBytes = null,
        ) }
    }

    private fun uploadProfilePicture(bytes: ByteArray?, mimeType: String?) {
        if(state.value.isUploadingImage) {
            return
        }

        if(bytes == null || mimeType == null) {
            return
        }

        _state.update { it.copy(
            isUploadingImage = true,
        ) }

        viewModelScope.launch {
            userRepository
                .uploadProfilePicture(
                    imageBytes = bytes,
                    mimeType = mimeType
                )
                .onSuccess {
                    _state.update { it.copy(
                        isUploadingImage = false,
                    ) }
                    eventChannel.send(ProfilePictureEvent.UploadSuccess)
                }
                .onFailure { error ->
                    _state.update { it.copy(
                        isUploadingImage = false
                    ) }
                    snackbarController.show(error.toSnackbarMessage())
                    eventChannel.send(ProfilePictureEvent.UploadError)
                }
        }
    }
}
