package com.kaanf.auth.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val gender: GenderDto? = null,
    val ageConfirmed: Boolean = false,
    val privacyAccepted: Boolean = false,
)
