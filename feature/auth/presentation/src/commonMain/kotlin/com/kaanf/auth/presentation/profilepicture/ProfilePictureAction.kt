package com.kaanf.auth.presentation.profilepicture

sealed interface ProfilePictureAction {
    data object OnUploadPictureClick: ProfilePictureAction
    class OnPictureSelected(val bytes: ByteArray, val mimeType: String?): ProfilePictureAction
    data object OnConfirmClick: ProfilePictureAction
}
