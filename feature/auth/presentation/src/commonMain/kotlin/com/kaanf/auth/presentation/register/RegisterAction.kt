package com.kaanf.auth.presentation.register

import com.kaanf.auth.domain.model.Gender

sealed interface RegisterAction {
    data object OnLoginClick : RegisterAction

    data object OnRegisterClick : RegisterAction

    data object OnTogglePasswordVisibilityClick : RegisterAction

    data object OnTermsToggle : RegisterAction

    data class OnGenderSelect(val gender: Gender) : RegisterAction

    data object OnUnderageGoBack : RegisterAction
}
