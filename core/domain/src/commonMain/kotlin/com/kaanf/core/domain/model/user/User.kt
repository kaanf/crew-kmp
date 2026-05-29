package com.kaanf.core.domain.model.user

data class User(
    val id: String,
    val email: String,
    val fullName: String? = null,
    val dateOfBirth: String,
    val gender: String,
    val role: String,
    val status: String,
    val hasVerifiedEmail: Boolean
) {
    val isCharacterCreated: Boolean
        get() = fullName != null
}
