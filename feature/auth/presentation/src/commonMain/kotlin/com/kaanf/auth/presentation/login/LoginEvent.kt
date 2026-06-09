package com.kaanf.auth.presentation.login

import com.kaanf.core.designsystem.component.layout.SnackbarMessage

sealed interface LoginEvent {
    data object NavigateToRegister : LoginEvent

    data class ShowSnackbar(val message: SnackbarMessage) : LoginEvent

    data object NavigateToForgotPassword : LoginEvent

    data object NavigateToDashboard : LoginEvent

    data object NavigateToProfilePicture : LoginEvent
}
