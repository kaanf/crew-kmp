package com.kaanf.crew.screenshots

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalComposeUiApi::class)
class ScreenshotActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Locale.setDefault(Locale.US)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        val config = resources.configuration
        config.setLocale(Locale.US)
        config.fontScale = 1f
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
        enableEdgeToEdge(SystemBarStyle.dark(android.graphics.Color.TRANSPARENT), SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        ScreenshotRouter.open(intent.dataString ?: "crew-screenshot://dashboard")
        setContent {
            Box(Modifier.semantics { testTagsAsResourceId = true }) { ScreenshotApp() }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        ScreenshotRouter.open(intent.dataString ?: "crew-screenshot://dashboard")
    }
}
