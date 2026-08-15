package com.kaanf.home.presentation.profile

import com.kaanf.core.domain.model.settings.AppLanguage

data class ProfileState(
    // Server-side values, mirrored from the session.
    val profilePictureUrl: String? = null,
    val fullName: String = "",
    val email: String = "",
    // Locally-persisted app language.
    val language: AppLanguage = AppLanguage.DEFAULT,
    // Staged edits, applied to the backend only when "Save your changes" is pressed.
    val editedName: String? = null,
    val pendingPhotoBytes: ByteArray? = null,
    val pendingPhotoRemoval: Boolean = false,
    // Transient: bytes currently open in the cropper, before they become a staged photo.
    val pendingCropBytes: ByteArray? = null,
    val isSaving: Boolean = false,
    val isDeletingAccount: Boolean = false,
) {
    val displayedName: String
        get() = editedName ?: fullName

    val isCropping: Boolean
        get() = pendingCropBytes != null

    val hasPhoto: Boolean
        get() = when {
            pendingPhotoRemoval -> false
            pendingPhotoBytes != null -> true
            else -> !profilePictureUrl.isNullOrBlank()
        }

    val nameChanged: Boolean
        get() = editedName != null && editedName.trim().isNotBlank() && editedName.trim() != fullName

    val photoChanged: Boolean
        get() = pendingPhotoBytes != null || (pendingPhotoRemoval && !profilePictureUrl.isNullOrBlank())

    val hasUnsavedChanges: Boolean
        get() = nameChanged || photoChanged

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProfileState) return false

        if (profilePictureUrl != other.profilePictureUrl) return false
        if (fullName != other.fullName) return false
        if (email != other.email) return false
        if (language != other.language) return false
        if (editedName != other.editedName) return false
        if (!byteArrayEquals(pendingPhotoBytes, other.pendingPhotoBytes)) return false
        if (pendingPhotoRemoval != other.pendingPhotoRemoval) return false
        if (!byteArrayEquals(pendingCropBytes, other.pendingCropBytes)) return false
        if (isSaving != other.isSaving) return false
        if (isDeletingAccount != other.isDeletingAccount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = profilePictureUrl?.hashCode() ?: 0
        result = 31 * result + fullName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + (editedName?.hashCode() ?: 0)
        result = 31 * result + (pendingPhotoBytes?.contentHashCode() ?: 0)
        result = 31 * result + pendingPhotoRemoval.hashCode()
        result = 31 * result + (pendingCropBytes?.contentHashCode() ?: 0)
        result = 31 * result + isSaving.hashCode()
        result = 31 * result + isDeletingAccount.hashCode()
        return result
    }
}

private fun byteArrayEquals(a: ByteArray?, b: ByteArray?): Boolean {
    if (a == null) return b == null
    if (b == null) return false
    return a.contentEquals(b)
}
