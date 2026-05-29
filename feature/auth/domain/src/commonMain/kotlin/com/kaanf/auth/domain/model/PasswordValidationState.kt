package com.kaanf.auth.domain.model

data class PasswordValidationState(
    val hasMinLength: Boolean = false,
    val hasDigit: Boolean = false,
    val hasUppercase: Boolean = false,
) {
    val isValidPassword: Boolean
        get() = hasMinLength && hasDigit && hasUppercase
}
