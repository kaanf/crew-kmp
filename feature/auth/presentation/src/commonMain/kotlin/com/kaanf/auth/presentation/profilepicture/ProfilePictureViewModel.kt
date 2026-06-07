package com.kaanf.auth.presentation.profilepicture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.repository.UserRepository
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfilePictureViewModel(
    private val userRepository: UserRepository
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
            is ProfilePictureAction.OnPictureSelected -> onPictureSelected(action.bytes, action.mimeType)
            is ProfilePictureAction.OnConfirmClick -> uploadProfilePicture(
                bytes = state.value.selectedImageBytes,
                mimeType = state.value.selectedMimeType,
            )
            else -> Unit
        }
    }

    private fun onPictureSelected(bytes: ByteArray, mimeType: String?) {
        _state.update { it.copy(
            selectedImageBytes = bytes,
            selectedMimeType = mimeType,
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
                    eventChannel.send(ProfilePictureEvent.UploadError)
                }
        }
    }
}
