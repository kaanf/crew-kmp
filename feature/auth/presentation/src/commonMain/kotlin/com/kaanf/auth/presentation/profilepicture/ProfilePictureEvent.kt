package com.kaanf.auth.presentation.profilepicture

sealed interface ProfilePictureEvent {
    data object UploadSuccess : ProfilePictureEvent
    data object UploadError : ProfilePictureEvent
}
