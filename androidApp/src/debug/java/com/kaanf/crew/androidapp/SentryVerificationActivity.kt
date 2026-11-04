package com.kaanf.crew.androidapp

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kaanf.crew.verifyLocalSentry
import io.sentry.Sentry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** Developer utility, excluded from release APKs. Uses no user/session data. */
class SentryVerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val origin = intent.getStringExtra("base_url") ?: "http://10.0.2.2:18096"
        val status = TextView(this).apply { text = "Local Sentry verification" }
        val button = Button(this).apply { text = "Send Sentry verification" }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
                val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val spacing = (16 * resources.displayMetrics.density).toInt()
                view.setPadding(spacing, bars.top + spacing, spacing, bars.bottom + spacing)
                insets
            }
            addView(status)
            addView(button)
        })
        button.setOnClickListener {
            if (!Sentry.isEnabled()) {
                status.text = "Configure the mobile Sentry DSN and rebuild first."
                return@setOnClickListener
            }
            button.isEnabled = false
            status.text = "Sending local verification request…"
            lifecycleScope.launch {
                val options = Sentry.getCurrentScopes().options
                val previousDebug = options.isDebug
                options.isDebug = true
                try {
                    val id = verifyLocalSentry(origin)
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { Sentry.flush(5_000) }
                    android.util.Log.i("CrewSentryVerification", "HTTP 500; request_id=$id")
                    status.text = "HTTP 500 received. Search request_id in both Sentry projects:\n$id"
                } catch (error: CancellationException) {
                    throw error
                } catch (_: Exception) {
                    android.util.Log.w("CrewSentryVerification", "Local verification failed")
                    status.text = "Verification failed. Check the local server and loopback address."
                } finally {
                    options.isDebug = previousDebug
                    button.isEnabled = true
                }
            }
        }
        // Explicit adb automation; never triggered by ordinary app launches/restoration.
        if (savedInstanceState == null && intent.getBooleanExtra("send_verification", false)) {
            intent.removeExtra("send_verification")
            button.performClick()
        }
    }
}
