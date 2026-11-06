package com.kaanf.core.domain.analytics

import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/** A foreground session survives short background trips and Activity recreation. */
class ForegroundAnalytics(
    private val analytics: Analytics,
    private val now: () -> Instant = { Clock.System.now() },
) {
    private var opened = false
    private var backgroundedAt: Instant? = null

    fun onForeground() {
        val previous = backgroundedAt
        if (!opened || (previous != null && now() - previous >= 30.minutes)) {
            opened = true
            analytics.track(AnalyticsEvent.AppOpened)
        }
        backgroundedAt = null
    }

    fun onBackground() {
        backgroundedAt = now()
    }
}
