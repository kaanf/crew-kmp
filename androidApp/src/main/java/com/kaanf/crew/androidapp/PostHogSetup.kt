package com.kaanf.crew.androidapp

import com.kaanf.crew.BuildConfig
import com.kaanf.core.data.analytics.ProductAnalytics
import com.posthog.PostHog
import com.posthog.PersonProfiles
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig

internal fun CrewApplication.initializePostHog() {
    if (!BuildConfig.POSTHOG_ENABLED || (BuildConfig.DEBUG && !BuildConfig.POSTHOG_DEBUG_ENABLED)) return
    if (!BuildConfig.POSTHOG_KEY.startsWith("phc_")) return
    if (BuildConfig.POSTHOG_HOST !in setOf("https://eu.i.posthog.com", "https://us.i.posthog.com")) return
    try {
        val config = PostHogAndroidConfig(BuildConfig.POSTHOG_KEY, BuildConfig.POSTHOG_HOST).apply {
            captureApplicationLifecycleEvents = false
            captureScreenViews = false
            captureDeepLinks = false
            captureElementInteractions = false
            capturePushNotificationSubscriptions = false
            capturePushNotificationOpened = false
            sessionReplay = false
            surveys = false
            preloadFeatureFlags = false
            sendFeatureFlagEvent = false
            setDefaultPersonProperties = false
            personProfiles = PersonProfiles.NEVER
            errorTrackingConfig.autoCapture = false
            addBeforeSend { event ->
                if (event.event !in productEventNames) null else event.copy(
                    properties = event.properties?.filterKeys { it in productPropertyNames }?.toMutableMap()?.apply {
                        put("\$geoip_disable", true)
                    },
                )
            }
        }
        PostHogAndroid.setup(this, config)
        ProductAnalytics.capture = { name, properties ->
            PostHog.capture(name, properties = properties + mapOf(
                "platform" to "android", "app_version" to BuildConfig.VERSION_NAME,
            ))
        }
    } catch (_: Exception) {
        // Telemetry setup must never prevent application startup.
    }
}

private val productEventNames = setOf(
    "app_opened", "screen_viewed", "task_selected", "match_started",
    "match_completed", "match_abandoned", "quest_opened",
)
private val productPropertyNames = setOf(
    "screen_name", "task_category", "platform", "app_version",
    "\$session_id", "\$lib", "\$lib_version", "\$process_person_profile",
)
