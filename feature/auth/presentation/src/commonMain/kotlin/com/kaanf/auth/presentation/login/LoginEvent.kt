package com.kaanf.auth.presentation.login

sealed interface LoginEvent {
    data object NavigateToRegister : LoginEvent

    data object NavigateToForgotPassword : LoginEvent

    data object NavigateToDashboard : LoginEvent

    data object NavigateToProfilePicture : LoginEvent
}
