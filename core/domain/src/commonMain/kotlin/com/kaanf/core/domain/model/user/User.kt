package com.kaanf.core.domain.model.user

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val gender: String,
    val role: String,
    val status: String,
    val isProfileComplete: Boolean,
    val hasVerifiedEmail: Boolean
)
