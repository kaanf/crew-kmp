package com.kaanf.crew.androidapp

import com.kaanf.crew.BuildConfig
import com.kaanf.core.data.networking.HttpDiagnostics
import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid

internal fun CrewApplication.initializeSentry() {
    if (BuildConfig.SENTRY_DSN.isBlank()) return
    SentryAndroid.init(this) { options ->
        options.dsn = BuildConfig.SENTRY_DSN
        options.environment = if (BuildConfig.DEBUG) "development" else "production"
        options.release = "crew-mobile@${BuildConfig.VERSION_NAME}+${BuildConfig.VERSION_CODE}"
        options.isSendDefaultPii = false
        options.tracesSampleRate = 0.0
        options.isEnableAutoSessionTracking = false
        options.isEnableUserInteractionTracing = false
        options.isEnableActivityLifecycleTracingAutoFinish = false
        options.isAttachScreenshot = false
        options.isAttachViewHierarchy = false
        options.sessionReplay.sessionSampleRate = 0.0
        options.sessionReplay.onErrorSampleRate = 0.0
        options.setBeforeBreadcrumb { breadcrumb, _ ->
            breadcrumb.takeIf { it.category == "crew.http" }
        }
        options.setBeforeSend { event, _ ->
            event.request = null
            event.user = null
            event.serverName = null
            event.extras = null
            event.exceptions?.forEach { it.value = null }
            if (event.getTag("crew.safe_message") != "true") event.message = null
            event
        }
    }
    HttpDiagnostics.onResponse = { requestId, operation, status ->
        val breadcrumb = Breadcrumb().apply {
            category = "crew.http"
            setData("request_id", requestId)
            setData("operation", operation)
            setData("status", status)
        }
        Sentry.addBreadcrumb(breadcrumb)
        if (status >= 500) {
            Sentry.captureMessage("Crew API request failed", SentryLevel.WARNING) { scope ->
                scope.setTag("request_id", requestId)
                scope.setTag("operation", operation)
                scope.setTag("http.status_code", status.toString())
                scope.setTag("crew.safe_message", "true")
            }
        }
    }
}
