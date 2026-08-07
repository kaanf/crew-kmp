package com.kaanf.auth.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LinkIdentityRequest(
    // Backend IdentityProvider enum adları: "Google" | "Apple"
    val provider: String,
    val idToken: String,
    val nonce: String,
)
