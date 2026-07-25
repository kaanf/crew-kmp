package com.kaanf.auth.domain.model

data class SocialLoginParams(
    val provider: SocialProvider,
    val idToken: String,
    val nonce: String,
    val ageConfirmed: Boolean,
    val privacyAccepted: Boolean,
    // Apple returns the name only on the very first authorization.
    val fullName: String? = null,
)
