package com.kaanf.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LinkedIdentityDto(
    // Backend IdentityProvider enum adları: "Google" | "Apple"
    val provider: String,
    val email: String? = null,
)

@Serializable
data class SignInMethodsResponse(
    val email: String,
    val hasPassword: Boolean,
    // Backend SignUpMethod enum adları: "Email" | "Google" | "Apple"
    val signUpMethod: String,
    val identities: List<LinkedIdentityDto>,
)
