package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmProfilePictureRequest(
    val publicUrl: String
)
