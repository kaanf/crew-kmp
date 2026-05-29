package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserSerializable(
    val id: String = "",
    val email: String,
    val fullName: String? = null,
    val dateOfBirth: String = "",
    val gender: String = "",
    val role: String = "",
    val status: String = "",
    val hasVerifiedEmail: Boolean = false,
)
