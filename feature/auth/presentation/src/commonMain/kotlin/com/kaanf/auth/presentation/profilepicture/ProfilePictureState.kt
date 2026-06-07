package com.kaanf.auth.presentation.profilepicture

data class ProfilePictureState(
    val profilePictureUrl: String? = null,
    val selectedImageBytes: ByteArray? = null,
    val selectedMimeType: String? = null,
    val isUploadingImage: Boolean = false,
    val isDeletingImage: Boolean = false,
) {
    val hasSelectedImage: Boolean
        get() = selectedImageBytes != null

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
        if (isUploadingImage != other.isUploadingImage) return false
        if (isDeletingImage != other.isDeletingImage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = profilePictureUrl?.hashCode() ?: 0
        result = 31 * result + (selectedImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + (selectedMimeType?.hashCode() ?: 0)
        result = 31 * result + isUploadingImage.hashCode()
        result = 31 * result + isDeletingImage.hashCode()
        return result
    }
}
