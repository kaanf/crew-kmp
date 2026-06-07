package com.kaanf.core.domain.model

data class ProfilePictureUploadUrls(
    val uploadUrl: String,
    val publicUrl: String,
    val headers: Map<String, String>
)
