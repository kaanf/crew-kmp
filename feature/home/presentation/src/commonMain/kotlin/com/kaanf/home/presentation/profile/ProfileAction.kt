package com.kaanf.home.presentation.profile

import com.kaanf.core.domain.model.settings.AppLanguage

sealed interface ProfileAction {
    data object OnChangePhotoClick : ProfileAction
    data object OnRemovePhotoClick : ProfileAction
    class OnPhotoPicked(val bytes: ByteArray, val mimeType: String?) : ProfileAction
    class OnCropConfirmed(val bytes: ByteArray) : ProfileAction
    data object OnCropCancelled : ProfileAction
    data object OnEditNameClick : ProfileAction
    class OnNameEdited(val name: String) : ProfileAction
    data object OnSaveChanges : ProfileAction
    data object OnLanguageClick : ProfileAction
    class OnLanguageSelected(val language: AppLanguage) : ProfileAction
    data object OnSignOutClick : ProfileAction
}
