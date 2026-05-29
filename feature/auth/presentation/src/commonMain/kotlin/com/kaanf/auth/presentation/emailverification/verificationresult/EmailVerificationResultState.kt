package com.kaanf.auth.presentation.emailverification.verificationresult

data class EmailVerificationResultState(
    val phase: EmailVerificationPhase = EmailVerificationPhase.Verifying,
)

enum class EmailVerificationPhase {
    Verifying,
    Verified,
    Failed,
}
