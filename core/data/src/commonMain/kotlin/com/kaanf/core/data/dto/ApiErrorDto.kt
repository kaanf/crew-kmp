package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

/** Backend hata zarfı: {"code":"EVENT_NOT_OPEN_FOR_TICKETS","message":"..."}. */
@Serializable
data class ApiErrorDto(
    val code: String,
    val message: String? = null,
)
