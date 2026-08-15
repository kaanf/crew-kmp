package com.kaanf.home.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.model.user.User
import com.kaanf.core.domain.repository.AuthSessionRepository
import com.kaanf.core.domain.repository.LanguageStore
import com.kaanf.core.domain.repository.UserRepository
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.core.presentation.util.UIText
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.profile_account_deleted_description
import crew.feature.home.presentation.generated.resources.profile_account_deleted_title
import crew.feature.home.presentation.generated.resources.profile_changes_saved_description
import crew.feature.home.presentation.generated.resources.profile_changes_saved_title
import crew.feature.home.presentation.generated.resources.profile_save_error_description
import crew.feature.home.presentation.generated.resources.profile_save_error_title
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authSessionRepository: AuthSessionRepository,
    private val languageStore: LanguageStore,
    private val snackbarController: SnackbarController,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state =
        _state.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState(),
        )

    // Latest user from the session, kept so name edits can be sent as a full User update.
    private var currentUser: User? = null

    init {
        observeCurrentUser()
        observeLanguage()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.OnChangePhotoClick -> Unit
            is ProfileAction.OnPhotoPicked -> onPhotoPicked(action.bytes)
            is ProfileAction.OnCropConfirmed -> onCropConfirmed(action.bytes)
            is ProfileAction.OnCropCancelled -> onCropCancelled()
            is ProfileAction.OnRemovePhotoClick -> onRemovePhotoClick()
            is ProfileAction.OnEditNameClick -> Unit
            is ProfileAction.OnNameEdited -> onNameEdited(action.name)
            is ProfileAction.OnSaveChanges -> saveChanges()
            is ProfileAction.OnCancelEdit -> cancelEdit()
            is ProfileAction.OnSignInMethodsClick -> Unit
            is ProfileAction.OnSignOutClick -> signOut()
            is ProfileAction.OnDeleteAccountClick -> Unit
            is ProfileAction.OnDeleteAccountConfirm -> deleteAccount()
        }
    }

    private fun observeLanguage() {
        languageStore
            .observeLanguage()
            .onEach { language -> _state.update { it.copy(language = language) } }
            .launchIn(viewModelScope)
    }

    private fun deleteAccount() {
        if (state.value.isDeletingAccount) return
        _state.update { it.copy(isDeletingAccount = true) }

        viewModelScope.launch {
            when (val result = authSessionRepository.deleteAccount()) {
                is Result.Success -> {
                    snackbarController.show(
                        SnackbarMessage(
                            title = UIText.Resource(Res.string.profile_account_deleted_title),
                            description = UIText.Resource(Res.string.profile_account_deleted_description),
                            variant = SnackbarVariant.Success,
                        ),
                    )
                    // Repository oturumu temizledi; navigasyonu MainViewModel'in session
                    // gözlemcisi yapıyor, burada ayrıca yönlendirme yok.
                    _state.update { it.copy(isDeletingAccount = false) }
                }

                is Result.Failure -> {
                    _state.update { it.copy(isDeletingAccount = false) }
                    snackbarController.show(result.error.toSnackbarMessage())
                }
            }
        }
    }

    private fun signOut() {
        // The screen navigates away on sign out, clearing this ViewModel and its scope, so the
        // logout (server revoke + local session clear) runs NonCancellable to always complete.
        viewModelScope.launch {
            withContext(NonCancellable) {
                authSessionRepository.logout()
            }
        }
    }

    private fun observeCurrentUser() {
        userRepository
            .observeCurrentUser()
            .onEach { user ->
                currentUser = user
                _state.update {
                    it.copy(
                        profilePictureUrl = user?.profilePictureUrl,
                        fullName = user?.fullName.orEmpty(),
                        email = user?.email.orEmpty(),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onNameEdited(name: String) {
        _state.update { it.copy(editedName = name) }
    }

    private fun onPhotoPicked(bytes: ByteArray) {
        _state.update { it.copy(pendingCropBytes = bytes) }
    }

    private fun onCropCancelled() {
        _state.update { it.copy(pendingCropBytes = null) }
    }

    // Staging only — the cropped photo is held locally until "Save your changes" is pressed.
    private fun onCropConfirmed(bytes: ByteArray) {
        _state.update {
            it.copy(
                pendingCropBytes = null,
                pendingPhotoBytes = bytes,
                pendingPhotoRemoval = false,
            )
        }
    }

    private fun onRemovePhotoClick() {
        _state.update {
            it.copy(
                pendingPhotoBytes = null,
                pendingPhotoRemoval = true,
            )
        }
    }

    // Drop every staged edit and leave edit mode; server-side values remain untouched.
    private fun cancelEdit() {
        if (state.value.isSaving) return
        _state.update {
            it.copy(
                editedName = null,
                pendingPhotoBytes = null,
                pendingPhotoRemoval = false,
                pendingCropBytes = null,
            )
        }
    }

    private fun saveChanges() {
        val snapshot = state.value
        if (snapshot.isSaving || !snapshot.hasUnsavedChanges) return

        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val success = commitChanges(snapshot)
            if (success) {
                // Server values flow back in via observeCurrentUser; just drop the staged edits.
                _state.update {
                    it.copy(
                        isSaving = false,
                        editedName = null,
                        pendingPhotoBytes = null,
                        pendingPhotoRemoval = false,
                    )
                }
                snackbarController.show(
                    SnackbarMessage(
                        title = UIText.Resource(Res.string.profile_changes_saved_title),
                        description = UIText.Resource(Res.string.profile_changes_saved_description),
                        variant = SnackbarVariant.Success,
                    ),
                )
            } else {
                _state.update { it.copy(isSaving = false) }
                snackbarController.show(
                    SnackbarMessage(
                        title = UIText.Resource(Res.string.profile_save_error_title),
                        description = UIText.Resource(Res.string.profile_save_error_description),
                        variant = SnackbarVariant.Error,
                    ),
                )
            }
        }
    }

    // Name first, then the photo, so the photo operation stays the authoritative last write and the
    // name PATCH never clobbers a freshly uploaded/removed picture.
    private suspend fun commitChanges(snapshot: ProfileState): Boolean {
        if (snapshot.nameChanged) {
            val user = currentUser ?: return false
            val result = userRepository.updateUser(user.copy(fullName = snapshot.editedName!!.trim()))
            if (result is Result.Failure) return false
        }

        when {
            snapshot.pendingPhotoBytes != null -> {
                val result = userRepository.uploadProfilePicture(
                    imageBytes = snapshot.pendingPhotoBytes,
                    mimeType = "image/webp",
                )
                if (result is Result.Failure) return false
            }

            snapshot.pendingPhotoRemoval && !snapshot.profilePictureUrl.isNullOrBlank() -> {
                val result = userRepository.deleteProfilePicture()
                if (result is Result.Failure) return false
            }
        }

        return true
    }
}
