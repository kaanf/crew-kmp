package com.kaanf.auth.presentation.social

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kaanf.auth.domain.model.SocialProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
actual fun rememberSocialSignInLauncher(
    onResult: (SocialSignInResult) -> Unit,
): SocialSignInLauncher {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentOnResult by rememberUpdatedState(onResult)
    val lifecycleOwner = LocalLifecycleOwner.current

    // Kullanıcı Custom Tab'ı deep link dönmeden kapatırsa callback hiç gelmez;
    // ekrana dönüşte (ON_RESUME) kısa bir bekleme sonrası hâlâ bekleyen akış varsa iptal say.
    // ponytail: 600ms gecikme, onNewIntent'in resume'dan önce işlenmesini garantiye alır.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && AppleSignInBridge.pendingCallback != null) {
                scope.launch {
                    delay(600)
                    if (AppleSignInBridge.pendingCallback != null) {
                        AppleSignInBridge.pendingCallback = null
                        currentOnResult(SocialSignInResult.Cancelled)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    return remember {
        SocialSignInLauncher { provider ->
            when (provider) {
                SocialProvider.Google -> scope.launch { currentOnResult(signInWithGoogle(context)) }
                SocialProvider.Apple -> launchAppleWebFlow(context) { currentOnResult(it) }
            }
        }
    }
}

private suspend fun signInWithGoogle(context: Context): SocialSignInResult {
    val nonce = randomNonce()

    return try {
        val option =
            GetGoogleIdOption.Builder()
                .setServerClientId(SocialAuthConfig.GOOGLE_SERVER_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .setNonce(nonce)
                .build()

        val response =
            CredentialManager.create(context).getCredential(
                context = context,
                request = GetCredentialRequest.Builder().addCredentialOption(option).build(),
            )

        val credential = GoogleIdTokenCredential.createFrom(response.credential.data)

        SocialSignInResult.Success(
            provider = SocialProvider.Google,
            idToken = credential.idToken,
            nonce = nonce,
            fullName = credential.displayName,
        )
    } catch (e: GetCredentialCancellationException) {
        SocialSignInResult.Cancelled
    } catch (e: GetCredentialException) {
        SocialSignInResult.Failed
    }
}

private fun launchAppleWebFlow(
    context: Context,
    onResult: (SocialSignInResult) -> Unit,
) {
    val nonce = randomNonce()
    val state = randomNonce()

    AppleSignInBridge.pendingCallback = { callbackUri ->
        onResult(parseAppleCallback(callbackUri, expectedState = state, nonce = nonce))
    }

    val url =
        Uri.parse("https://appleid.apple.com/auth/authorize")
            .buildUpon()
            .appendQueryParameter("client_id", SocialAuthConfig.APPLE_SERVICE_ID)
            .appendQueryParameter("redirect_uri", SocialAuthConfig.APPLE_WEB_REDIRECT_URI)
            .appendQueryParameter("response_type", "code id_token")
            .appendQueryParameter("scope", "name email")
            .appendQueryParameter("response_mode", "form_post")
            .appendQueryParameter("state", state)
            .appendQueryParameter("nonce", nonce)
            .build()

    CustomTabsIntent.Builder().build().launchUrl(context, url)
}

private fun parseAppleCallback(
    callbackUri: String,
    expectedState: String,
    nonce: String,
): SocialSignInResult {
    val uri = Uri.parse(callbackUri)
    val error = uri.getQueryParameter("error")
    val idToken = uri.getQueryParameter("id_token")

    return when {
        error == "user_cancelled_authorize" -> SocialSignInResult.Cancelled
        error != null || idToken == null -> SocialSignInResult.Failed
        uri.getQueryParameter("state") != expectedState -> SocialSignInResult.Failed
        else ->
            SocialSignInResult.Success(
                provider = SocialProvider.Apple,
                idToken = idToken,
                nonce = nonce,
                fullName = parseAppleUserName(uri.getQueryParameter("user")),
            )
    }
}

// Apple, adı yalnız ilk yetkilendirmede `user` JSON'u olarak döner: {"name":{"firstName","lastName"},...}
private fun parseAppleUserName(userJson: String?): String? {
    if (userJson.isNullOrBlank()) return null

    return runCatching {
        val name = JSONObject(userJson).optJSONObject("name") ?: return null
        listOf(name.optString("firstName"), name.optString("lastName"))
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { null }
    }.getOrNull()
}
