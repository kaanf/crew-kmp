package com.kaanf.home.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.model.settings.AppLanguage
import com.kaanf.core.domain.model.user.User
import com.kaanf.core.domain.repository.AuthSessionRepository
import com.kaanf.core.domain.repository.LanguageStore
import com.kaanf.core.domain.repository.UserRepository
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.util.UIText
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.profile_changes_saved_description
import crew.feature.home.presentation.generated.resources.profile_changes_saved_title
import crew.feature.home.presentation.generated.resources.profile_save_error_description
import crew.feature.home.presentation.generated.resources.profile_save_error_title
import kotlinx.coroutines.NonCancellable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
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
            is ProfileAction.OnLanguageClick -> Unit
            is ProfileAction.OnLanguageSelected -> onLanguageSelected(action.language)
            is ProfileAction.OnSignOutClick -> signOut()
        }
    }

    private fun observeLanguage() {
        languageStore
            .observeLanguage()
            .onEach { language -> _state.update { it.copy(language = language) } }
            .launchIn(viewModelScope)
    }

    private fun onLanguageSelected(language: AppLanguage) {
        viewModelScope.launch { languageStore.setLanguage(language) }
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
                        gender = formatGender(user?.gender),
                        dateOfBirth = formatDateOfBirth(user?.dateOfBirth),
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

// "MALE" -> "Male", "prefer_not_to_say" -> "Prefer not to say".
private fun formatGender(raw: String?): String {
    val value = raw?.trim().orEmpty()
    if (value.isBlank()) return ""
    return value
        .replace('_', ' ')
        .lowercase()
        .replaceFirstChar { it.uppercase() }
}

private val MONTH_NAMES = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

// ISO "1998-05-12" -> "12 May 1998"; falls back to the raw value if it can't be parsed.
private fun formatDateOfBirth(raw: String?): String {
    val value = raw?.trim().orEmpty()
    if (value.isBlank()) return ""
    return runCatching {
        val date = LocalDate.parse(value)
        "${date.day} ${MONTH_NAMES[date.month.number - 1]} ${date.year}"
    }.getOrDefault(value)
}
