package com.kaanf.auth.presentation.emailverification.verificationsent

sealed interface EmailVerificationSentAction {
    data object OnReturnToLoginClicked : EmailVerificationSentAction

    data object OnResendSignalClicked : EmailVerificationSentAction
}
