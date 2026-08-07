package com.kaanf.auth.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    // Sosyal girişle açılmış hesap ilk şifresini belirlerken null gönderilir.
    val oldPassword: String? = null,
    val newPassword: String,
)
