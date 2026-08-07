package com.kaanf.auth.presentation.signinmethods

import com.kaanf.auth.domain.model.SocialProvider

sealed interface SignInMethodsEvent {
    data class LaunchProvider(val provider: SocialProvider) : SignInMethodsEvent
}
