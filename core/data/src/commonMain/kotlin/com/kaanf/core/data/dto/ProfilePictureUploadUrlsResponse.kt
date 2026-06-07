package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfilePictureUploadUrlsResponse(
    val uploadUrl: String,
    val publicUrl: String,
    val headers: Map<String, String>
)
