package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserSerializable(
    val id: String = "",
    val email: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val role: String = "",
    val status: String = "",
    val hasVerifiedEmail: Boolean = false,
    val isProfileComplete: Boolean = false,
)
