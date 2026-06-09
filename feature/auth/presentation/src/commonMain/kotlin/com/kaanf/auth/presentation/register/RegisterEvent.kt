package com.kaanf.auth.presentation.register

sealed interface RegisterEvent {
    data class RegisterSuccess(val email: String) : RegisterEvent
    data object NavigateToLogin : RegisterEvent
}
