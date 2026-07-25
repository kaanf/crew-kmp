package com.kaanf.auth.presentation.social

import androidx.compose.runtime.Composable
import com.kaanf.auth.domain.model.SocialProvider
import kotlin.random.Random

sealed interface SocialSignInResult {
    data class Success(
        val provider: SocialProvider,
        val idToken: String,
        // Provider'a gönderilen ham nonce; backend token içindeki claim ile eşitlik karşılaştırır.
        val nonce: String,
        val fullName: String?,
    ) : SocialSignInResult

    data object Cancelled : SocialSignInResult

    data object Failed : SocialSignInResult
}

class SocialSignInLauncher(
    private val onLaunch: (SocialProvider) -> Unit,
) {
    fun launch(provider: SocialProvider) = onLaunch(provider)
}

/**
 * Platformun kimlik akışını başlatır ve provider'dan alınan id_token'ı döndürür.
 * Android: Google = Credential Manager, Apple = Custom Tab web akışı.
 * iOS: Apple = AuthenticationServices, Google = ASWebAuthenticationSession + PKCE.
 */
@Composable
expect fun rememberSocialSignInLauncher(
    onResult: (SocialSignInResult) -> Unit,
): SocialSignInLauncher

internal fun randomNonce(): String =
    Random.nextBytes(16).joinToString("") { byte ->
        byte.toUByte().toString(16).padStart(2, '0')
    }
