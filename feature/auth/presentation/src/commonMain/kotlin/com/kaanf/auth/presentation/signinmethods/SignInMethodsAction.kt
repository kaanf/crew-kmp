package com.kaanf.auth.presentation.signinmethods

import com.kaanf.auth.domain.model.SocialProvider
import com.kaanf.auth.presentation.social.SocialSignInResult

sealed interface SignInMethodsAction {
    data class OnConnectClick(val provider: SocialProvider) : SignInMethodsAction
    data class OnSignInResult(val result: SocialSignInResult) : SignInMethodsAction
    data class OnUnlinkClick(val provider: SocialProvider) : SignInMethodsAction
    data object OnUnlinkConfirm : SignInMethodsAction
    data object OnUnlinkDismiss : SignInMethodsAction
    data object OnConflictDismiss : SignInMethodsAction
    data object OnPasswordClick : SignInMethodsAction
    data object OnPasswordSheetDismiss : SignInMethodsAction
    data class OnPasswordSubmit(
        val currentPassword: String,
        val newPassword: String,
    ) : SignInMethodsAction
}
