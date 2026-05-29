package com.kaanf.auth.presentation.emailverification.verificationsent

import com.kaanf.core.presentation.base.BaseEvent

sealed interface EmailVerificationSentEvent : BaseEvent {
    data object NavigateToLogin : EmailVerificationSentEvent
}
