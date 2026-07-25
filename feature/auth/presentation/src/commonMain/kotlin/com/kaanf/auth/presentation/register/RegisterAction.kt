package com.kaanf.auth.presentation.register

sealed interface RegisterAction {
    data object OnLoginClick : RegisterAction

    data object OnRegisterClick : RegisterAction

    data object OnTogglePasswordVisibilityClick : RegisterAction

    data object OnTermsToggle : RegisterAction

    data object OnAgeConfirmationToggle : RegisterAction

    data object OnUnderageGoBack : RegisterAction
}
