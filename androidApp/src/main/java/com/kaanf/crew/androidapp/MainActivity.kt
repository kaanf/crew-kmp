package com.kaanf.crew.androidapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kaanf.auth.presentation.social.AppleSignInBridge
import com.kaanf.crew.App

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        var isSplashScreenNeeded = true
        installSplashScreen().setKeepOnScreenCondition {
            isSplashScreenNeeded
        }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        dispatchAppleSignInIntent(intent)
        requestNotificationPermissionIfNeeded()

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

    // ponytail: izin açılışta direkt sorulur; rationale/uygun-an akışı gerekirse eklenir.
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun dispatchAppleSignInIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.scheme == "crew" && uri.host == "auth" && uri.path == "/apple") {
            AppleSignInBridge.onCallback(uri.toString())
        }
    }
}
