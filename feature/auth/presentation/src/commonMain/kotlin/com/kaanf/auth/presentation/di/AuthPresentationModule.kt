package com.kaanf.auth.presentation.di

import com.kaanf.auth.presentation.emailverification.verificationresult.EmailVerificationResultViewModel
import com.kaanf.auth.presentation.emailverification.verificationsent.EmailVerificationSentViewModel
import com.kaanf.auth.presentation.forgotpassword.ForgotPasswordViewModel
import com.kaanf.auth.presentation.login.LoginViewModel
import com.kaanf.auth.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule =
    module {
        viewModelOf(::LoginViewModel)
        viewModelOf(::RegisterViewModel)
        viewModelOf(::EmailVerificationSentViewModel)
        viewModelOf(::EmailVerificationResultViewModel)
        viewModelOf(::ForgotPasswordViewModel)
    }
