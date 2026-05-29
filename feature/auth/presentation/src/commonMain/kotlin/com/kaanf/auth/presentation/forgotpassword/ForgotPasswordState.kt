package com.kaanf.auth.presentation.forgotpassword

import androidx.compose.foundation.text.input.TextFieldState

data class ForgotPasswordState(
    val emailTextState: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false,
)
