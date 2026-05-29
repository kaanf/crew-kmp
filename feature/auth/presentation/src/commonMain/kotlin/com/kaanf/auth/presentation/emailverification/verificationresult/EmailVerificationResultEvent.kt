package com.kaanf.auth.presentation.emailverification.verificationresult

import com.kaanf.core.presentation.base.BaseEvent

sealed interface EmailVerificationResultEvent : BaseEvent {
    data object NavigateToResult : EmailVerificationResultEvent
}
