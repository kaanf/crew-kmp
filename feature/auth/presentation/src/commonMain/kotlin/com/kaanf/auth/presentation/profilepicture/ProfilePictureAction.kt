package com.kaanf.auth.presentation.profilepicture

sealed interface ProfilePictureAction {
    data object OnUploadPictureClick: ProfilePictureAction
    data object OnReCropClick: ProfilePictureAction
    data object OnRemoveClick: ProfilePictureAction
    class OnPictureSelected(val bytes: ByteArray, val mimeType: String?): ProfilePictureAction
    class OnCropConfirmed(val bytes: ByteArray): ProfilePictureAction
    data object OnCropCancelled: ProfilePictureAction
    data object OnConfirmClick: ProfilePictureAction
    data object OnSkipClick: ProfilePictureAction
}
