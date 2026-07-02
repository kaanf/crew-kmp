package com.kaanf.crew.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kaanf.crew.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var isSplashScreenNeeded = true
        installSplashScreen().setKeepOnScreenCondition {
            isSplashScreenNeeded
        }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // dispatchDeepLinkIntent(intent)

        setContent {
            App(
                onAuthenticationChecked = {
                    isSplashScreenNeeded = false
                }
            )
        }
    }

    /*
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        dispatchDeepLinkIntent(intent)
    }

    private fun dispatchDeepLinkIntent(intent: Intent?) {
        val uri = intent?.dataString ?: return
        ExternalUriHandler.onNewUri(uri)
    }
     */
}
