package com.kaanf.auth.presentation.emailverification.verificationsent

import com.kaanf.auth.presentation.emailverification.verificationresult.EmailVerificationPhase

data class EmailVerificationSentState(
    val registeredEmail: String = "",
    val isResending: Boolean = false,
    val isResendEnabled: Boolean = false,
    val resendCountdownSeconds: Int = 60,
    val phase: EmailVerificationPhase = EmailVerificationPhase.Verifying,
    val isVerifying: Boolean = false,
)
