package com.kaanf.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    @SerialName("token") val refreshToken: String
)
