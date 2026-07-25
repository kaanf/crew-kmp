package com.kaanf.crew.androidapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kaanf.auth.presentation.social.AppleSignInBridge
import com.kaanf.crew.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var isSplashScreenNeeded = true
        installSplashScreen().setKeepOnScreenCondition {
            isSplashScreenNeeded
        }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        dispatchAppleSignInIntent(intent)

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
    }

    private fun dispatchAppleSignInIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.scheme == "crew" && uri.host == "auth" && uri.path == "/apple") {
            AppleSignInBridge.onCallback(uri.toString())
        }
    }
}
