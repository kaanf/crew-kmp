package com.kaanf.auth.presentation.social

import com.kaanf.auth.domain.model.SocialProvider

sealed interface SocialLoginEvent {
    data class LaunchProvider(val provider: SocialProvider) : SocialLoginEvent
    data object NavigateToDashboard : SocialLoginEvent
    data object NavigateToProfilePicture : SocialLoginEvent
}
