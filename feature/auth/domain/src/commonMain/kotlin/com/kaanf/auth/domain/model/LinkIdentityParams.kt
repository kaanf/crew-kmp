package com.kaanf.auth.domain.model

data class LinkIdentityParams(
    val provider: SocialProvider,
    val idToken: String,
    val nonce: String,
)
