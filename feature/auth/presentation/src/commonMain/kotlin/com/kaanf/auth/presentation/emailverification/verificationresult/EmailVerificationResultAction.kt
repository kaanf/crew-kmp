package com.kaanf.auth.presentation.emailverification.verificationresult

sealed interface EmailVerificationResultAction {
    data object OnReturnToLoginClicked : EmailVerificationResultAction

    data object OnResendSignalClicked : EmailVerificationResultAction
}
