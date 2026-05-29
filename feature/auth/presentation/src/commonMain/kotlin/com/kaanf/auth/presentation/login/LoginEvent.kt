package com.kaanf.auth.presentation.login

import com.kaanf.core.presentation.base.BaseEvent

sealed interface LoginEvent : BaseEvent {
    data object NavigateToRegister : LoginEvent

    data object NavigateToForgotPassword : LoginEvent

    data object NavigateToDashboard : LoginEvent
}
