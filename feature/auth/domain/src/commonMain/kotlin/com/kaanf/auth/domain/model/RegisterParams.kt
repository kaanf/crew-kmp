package com.kaanf.auth.domain.model

data class RegisterParams(
    val email: String,
    val password: String,
    val fullName: String,
    val profilePictureUrl: String?,
    val ageConfirmed: Boolean = false,
    val privacyAccepted: Boolean = false,
)
