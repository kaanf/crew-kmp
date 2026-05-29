package com.kaanf.auth.presentation.register

import com.kaanf.core.presentation.base.BaseEvent

sealed interface RegisterEvent : BaseEvent {
    data class RegisterSuccess(val email: String) : RegisterEvent
    data object NavigateToLogin : RegisterEvent
}
