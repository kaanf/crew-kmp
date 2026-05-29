package com.kaanf.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenericBooleanResponse(
    @SerialName("success")
    val isSuccess: Boolean
)
