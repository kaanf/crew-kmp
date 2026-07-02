package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

// Mirrors the backend PATCH /profile contract: every field is optional, and only the non-null
// ones are applied server-side.
@Serializable
data class UpdateUserRequest(
    val fullName: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val profilePictureUrl: String? = null,
)
