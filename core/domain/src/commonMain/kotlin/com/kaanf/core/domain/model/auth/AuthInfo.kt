package com.kaanf.core.domain.model.auth

import com.kaanf.core.domain.model.user.User

data class AuthInfo(
    val accessToken: String,
    val refreshToken: String,
    val user: User?
)
