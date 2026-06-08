package com.kaanf.auth.presentation.profilepicture

enum class ProfilePicturePhase { Picker, Crop }

data class ProfilePictureState(
    val profilePictureUrl: String? = null,
    val selectedImageBytes: ByteArray? = null,
    val selectedMimeType: String? = null,
    // Full-resolution picked source, retained so the user can re-crop the same photo. Never uploaded.
    val originalImageBytes: ByteArray? = null,
    val pendingCropBytes: ByteArray? = null,
    val isUploadingImage: Boolean = false,
    val isDeletingImage: Boolean = false,
) {
    val hasSelectedImage: Boolean
        get() = selectedImageBytes != null

    val phase: ProfilePicturePhase
        get() = if (pendingCropBytes != null) ProfilePicturePhase.Crop else ProfilePicturePhase.Picker

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProfilePictureState) return false

        if (profilePictureUrl != other.profilePictureUrl) return false
        if (selectedImageBytes != null) {
            if (other.selectedImageBytes == null) return false
            if (!selectedImageBytes.contentEquals(other.selectedImageBytes)) return false
        } else if (other.selectedImageBytes != null) {
            return false
        }
        if (selectedMimeType != other.selectedMimeType) return false
        if (originalImageBytes != null) {
            if (other.originalImageBytes == null) return false
            if (!originalImageBytes.contentEquals(other.originalImageBytes)) return false
        } else if (other.originalImageBytes != null) {
            return false
        }
        if (pendingCropBytes != null) {
            if (other.pendingCropBytes == null) return false
            if (!pendingCropBytes.contentEquals(other.pendingCropBytes)) return false
        } else if (other.pendingCropBytes != null) {
            return false
        }
        if (isUploadingImage != other.isUploadingImage) return false
        if (isDeletingImage != other.isDeletingImage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = profilePictureUrl?.hashCode() ?: 0
        result = 31 * result + (selectedImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + (selectedMimeType?.hashCode() ?: 0)
        result = 31 * result + (originalImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + (pendingCropBytes?.contentHashCode() ?: 0)
        result = 31 * result + isUploadingImage.hashCode()
        result = 31 * result + isDeletingImage.hashCode()
        return result
    }
}
