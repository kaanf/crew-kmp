package com.kaanf.auth.presentation.social

import com.kaanf.auth.domain.model.SocialProvider

sealed interface SocialLoginAction {
    data class OnProviderClick(val provider: SocialProvider) : SocialLoginAction
    data object OnConsentDismiss : SocialLoginAction
    data object OnConsentConfirm : SocialLoginAction
    data class OnSignInResult(val result: SocialSignInResult) : SocialLoginAction
}
