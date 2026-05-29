package com.kaanf.auth.presentation.login

sealed interface LoginAction {
    data object OnLoginClick : LoginAction

    data object OnRegisterClick : LoginAction

    data object OnForgotPasswordClick : LoginAction
}
