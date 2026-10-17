package com.kaanf.crew.androidapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kaanf.auth.presentation.social.AppleSignInBridge
import com.kaanf.core.domain.review.attachAppReviewActivity
import com.kaanf.crew.App
import com.kaanf.crew.navigation.ExternalUriHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var isSplashScreenNeeded = true
        installSplashScreen().setKeepOnScreenCondition {
            isSplashScreenNeeded
        }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        dispatchAppleSignInIntent(intent)
        if (savedInstanceState == null) {
            dispatchPushLink(intent)
            dispatchEventLink(intent)
        }
        attachAppReviewActivity(this)

        setContent {
            App(
                onAuthenticationChecked = {
                    isSplashScreenNeeded = false
                }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        dispatchAppleSignInIntent(intent)
        dispatchPushLink(intent)
        dispatchEventLink(intent)
    }

    /**
     * Bildirime tıklanınca FCM data'sı intent extras olarak gelir: arka plandayken
     * FCM'in kendi gösterdiği bildirimde de, MyFirebaseMessagingService'inkinde de.
     */
    private fun dispatchPushLink(intent: Intent) {
        val link = intent.getStringExtra(PUSH_LINK_KEY) ?: return
        intent.removeExtra(PUSH_LINK_KEY)
        ExternalUriHandler.onNewUri(link)
    }

    private fun dispatchEventLink(intent: Intent) {
        val uri = intent.data ?: return
        if (uri.scheme == "crew" && uri.host == "event") {
            ExternalUriHandler.onNewUri(uri.toString())
            intent.data = null
        }
    }

    private fun dispatchAppleSignInIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.scheme == "crew" && uri.host == "auth" && uri.path == "/apple") {
            AppleSignInBridge.onCallback(uri.toString())
        }
    }
}
