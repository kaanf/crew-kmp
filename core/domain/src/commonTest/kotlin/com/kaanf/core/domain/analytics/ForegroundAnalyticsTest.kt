package com.kaanf.core.domain.analytics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.time.Duration.Companion.minutes

class ForegroundAnalyticsTest {
    @Test
    fun shortBackgroundTripsDoNotDuplicateAppOpened() {
        val events = mutableListOf<AnalyticsEvent>()
        val analytics = object : Analytics {
            override fun track(event: AnalyticsEvent) { events += event }
        }
        var now = Instant.fromEpochMilliseconds(0)
        val sessions = ForegroundAnalytics(analytics) { now }
        sessions.onForeground()
        sessions.onForeground()
        sessions.onBackground()
        now += 29.minutes
        sessions.onForeground()
        assertEquals(listOf<AnalyticsEvent>(AnalyticsEvent.AppOpened), events)
        sessions.onBackground()
        now += 30.minutes
        sessions.onForeground()
        assertEquals(listOf<AnalyticsEvent>(AnalyticsEvent.AppOpened, AnalyticsEvent.AppOpened), events)
    }
}
