package com.kaanf.auth.presentation.social

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.kaanf.auth.domain.model.SocialProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationErrorCanceled
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.AuthenticationServices.ASWebAuthenticationSessionErrorCodeCanceledLogin
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.Foundation.NSCharacterSet
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.Foundation.NSURLSession
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Foundation.dataTaskWithRequest
import platform.Foundation.setHTTPBody
import platform.Foundation.setHTTPMethod
import platform.Foundation.setValue
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberSocialSignInLauncher(
    onResult: (SocialSignInResult) -> Unit,
): SocialSignInLauncher {
    val currentOnResult by rememberUpdatedState(onResult)

    return remember {
        SocialSignInLauncher { provider ->
            when (provider) {
                SocialProvider.Apple -> launchAppleSignIn { currentOnResult(it) }
                SocialProvider.Google -> launchGoogleSignIn { currentOnResult(it) }
            }
        }
    }
}

// Delegate/session referansları akış bitene kadar güçlü tutulmalı, yoksa ARC toplar.
private var appleDelegateRef: AppleSignInDelegate? = null
private var webAuthSessionRef: ASWebAuthenticationSession? = null
private var webAuthPresenterRef: WebAuthPresentationContextProvider? = null

// --- Apple: native AuthenticationServices ---

private fun launchAppleSignIn(onResult: (SocialSignInResult) -> Unit) {
    val nonce = randomNonce()

    val request = ASAuthorizationAppleIDProvider().createRequest().apply {
        requestedScopes = listOf(ASAuthorizationScopeFullName, ASAuthorizationScopeEmail)
        setNonce(nonce)
    }

    val delegate = AppleSignInDelegate(nonce = nonce) { result ->
        appleDelegateRef = null
        onResult(result)
    }
    appleDelegateRef = delegate

    ASAuthorizationController(authorizationRequests = listOf(request)).apply {
        setDelegate(delegate)
        setPresentationContextProvider(delegate)
        performRequests()
    }
}

private class AppleSignInDelegate(
    private val nonce: String,
    private val onResult: (SocialSignInResult) -> Unit,
) : NSObject(),
    ASAuthorizationControllerDelegateProtocol,
    ASAuthorizationControllerPresentationContextProvidingProtocol {

    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization,
    ) {
        val credential = didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
        val idToken = credential?.identityToken?.let {
            NSString.create(data = it, encoding = NSUTF8StringEncoding) as String?
        }

        if (credential == null || idToken == null) {
            onResult(SocialSignInResult.Failed)
            return
        }

        // Apple adı yalnız ilk yetkilendirmede verir; sonraki girişlerde null gelir.
        val fullName = credential.fullName?.let { components ->
            listOfNotNull(components.givenName, components.familyName)
                .joinToString(" ")
                .ifBlank { null }
        }

        onResult(
            SocialSignInResult.Success(
                provider = SocialProvider.Apple,
                idToken = idToken,
                nonce = nonce,
                fullName = fullName,
            )
        )
    }

    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError,
    ) {
        val cancelled = didCompleteWithError.code == ASAuthorizationErrorCanceled
        onResult(if (cancelled) SocialSignInResult.Cancelled else SocialSignInResult.Failed)
    }

    override fun presentationAnchorForAuthorizationController(
        controller: ASAuthorizationController,
    ): ASPresentationAnchor = UIApplication.sharedApplication.keyWindow ?: UIWindow()
}

// --- Google: ASWebAuthenticationSession + OAuth 2.0 Authorization Code + PKCE (SDK'sız, PRD §4.2) ---

private class WebAuthPresentationContextProvider :
    NSObject(),
    ASWebAuthenticationPresentationContextProvidingProtocol {
    override fun presentationAnchorForWebAuthenticationSession(
        session: ASWebAuthenticationSession,
    ): ASPresentationAnchor = UIApplication.sharedApplication.keyWindow ?: UIWindow()
}

private fun launchGoogleSignIn(onResult: (SocialSignInResult) -> Unit) {
    val nonce = randomNonce()
    val codeVerifier = randomNonce() + randomNonce() + randomNonce()
    val redirectUri = "${SocialAuthConfig.GOOGLE_IOS_REDIRECT_SCHEME}:/oauth2redirect"

    val components = NSURLComponents(string = "https://accounts.google.com/o/oauth2/v2/auth")
    components.queryItems = listOf(
        NSURLQueryItem(name = "client_id", value = SocialAuthConfig.GOOGLE_IOS_CLIENT_ID),
        NSURLQueryItem(name = "redirect_uri", value = redirectUri),
        NSURLQueryItem(name = "response_type", value = "code"),
        NSURLQueryItem(name = "scope", value = "openid email profile"),
        NSURLQueryItem(name = "code_challenge", value = codeVerifier.sha256Base64Url()),
        NSURLQueryItem(name = "code_challenge_method", value = "S256"),
        NSURLQueryItem(name = "nonce", value = nonce),
    )
    val url = components.URL ?: return onResult(SocialSignInResult.Failed)

    val session = ASWebAuthenticationSession(
        uRL = url,
        callbackURLScheme = SocialAuthConfig.GOOGLE_IOS_REDIRECT_SCHEME,
    ) { callbackUrl, error ->
        webAuthSessionRef = null
        webAuthPresenterRef = null

        if (error != null) {
            val cancelled = error.code == ASWebAuthenticationSessionErrorCodeCanceledLogin
            onResult(if (cancelled) SocialSignInResult.Cancelled else SocialSignInResult.Failed)
            return@ASWebAuthenticationSession
        }

        val code = callbackUrl
            ?.let { NSURLComponents(uRL = it, resolvingAgainstBaseURL = false) }
            ?.queryItems
            ?.filterIsInstance<NSURLQueryItem>()
            ?.firstOrNull { it.name == "code" }
            ?.value

        if (code == null) {
            onResult(SocialSignInResult.Failed)
        } else {
            exchangeGoogleCode(code, codeVerifier, redirectUri, nonce, onResult)
        }
    }

    // Kullanıcının mevcut Safari Google oturumu sürtünmeyi azaltsın (PRD §4.2).
    session.prefersEphemeralWebBrowserSession = false

    val presenter = WebAuthPresentationContextProvider()
    webAuthPresenterRef = presenter
    session.setPresentationContextProvider(presenter)
    webAuthSessionRef = session
    session.start()
}

// PKCE token exchange: iOS tipi client'ta secret yoktur, code_verifier yeterlidir.
@OptIn(ExperimentalForeignApi::class)
private fun exchangeGoogleCode(
    code: String,
    codeVerifier: String,
    redirectUri: String,
    nonce: String,
    onResult: (SocialSignInResult) -> Unit,
) {
    val tokenUrl = NSURL(string = "https://oauth2.googleapis.com/token")
    val request = NSMutableURLRequest(uRL = tokenUrl).apply {
        setHTTPMethod("POST")
        setValue("application/x-www-form-urlencoded", forHTTPHeaderField = "Content-Type")

        val body = listOf(
            "code" to code,
            "client_id" to SocialAuthConfig.GOOGLE_IOS_CLIENT_ID,
            "redirect_uri" to redirectUri,
            "grant_type" to "authorization_code",
            "code_verifier" to codeVerifier,
        ).joinToString("&") { (key, value) -> "$key=${value.formUrlEncoded()}" }

        setHTTPBody(body.toNSData())
    }

    NSURLSession.sharedSession.dataTaskWithRequest(request) { data, _, _ ->
        val idToken = data?.let { payload ->
            val json = NSJSONSerialization.JSONObjectWithData(payload, 0u, null) as? Map<*, *>
            json?.get("id_token") as? String
        }

        dispatch_async(dispatch_get_main_queue()) {
            if (idToken == null) {
                onResult(SocialSignInResult.Failed)
            } else {
                onResult(
                    SocialSignInResult.Success(
                        provider = SocialProvider.Google,
                        idToken = idToken,
                        nonce = nonce,
                        fullName = null,
                    )
                )
            }
        }
    }.resume()
}

// --- yardımcılar ---

@OptIn(ExperimentalForeignApi::class)
private fun String.sha256Base64Url(): String {
    val input = encodeToByteArray()
    val digest = ByteArray(CC_SHA256_DIGEST_LENGTH)

    input.usePinned { pinnedInput ->
        digest.usePinned { pinnedDigest ->
            CC_SHA256(
                pinnedInput.addressOf(0),
                input.size.convert(),
                pinnedDigest.addressOf(0).reinterpret(),
            )
        }
    }

    val base64 = digest.toNSData().base64EncodedStringWithOptions(0u)
    return base64.replace("+", "-").replace("/", "_").trimEnd('=')
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData =
    usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.convert())
    }

private fun String.toNSData(): NSData = encodeToByteArray().toNSData()

private val formUnreservedCharacters =
    NSCharacterSet.characterSetWithCharactersInString(
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
    )

@Suppress("CAST_NEVER_SUCCEEDS")
private fun String.formUrlEncoded(): String =
    (this as NSString).stringByAddingPercentEncodingWithAllowedCharacters(formUnreservedCharacters) ?: this
