package com.kaanf.core.data.analytics

import com.kaanf.core.domain.analytics.AnalyticsCategory
import com.kaanf.core.domain.analytics.AnalyticsEvent
import com.kaanf.core.domain.analytics.AnalyticsScreen
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductAnalyticsTest {
    @AfterTest
    fun cleanup() { ProductAnalytics.capture = null }

    @Test
    fun eventsContainOnlyBoundedProductProperties() {
        val sent = mutableListOf<Pair<String, Map<String, String>>>()
        ProductAnalytics.capture = { name, properties -> sent += name to properties }
        ProductAnalytics.track(AnalyticsEvent.ScreenViewed(AnalyticsScreen.Profile))
        ProductAnalytics.track(AnalyticsEvent.TaskSelected(AnalyticsCategory.Photo))
        ProductAnalytics.track(AnalyticsEvent.MatchCompleted)
        assertEquals(listOf(
            "screen_viewed" to mapOf("screen_name" to "profile"),
            "task_selected" to mapOf("task_category" to "photo"),
            "match_completed" to emptyMap(),
        ), sent)
    }

    @Test
    fun missingOrFailingSdkDoesNotInterruptUserActions() {
        ProductAnalytics.track(AnalyticsEvent.AppOpened)
        ProductAnalytics.capture = { _, _ -> error("SDK unavailable") }
        ProductAnalytics.track(AnalyticsEvent.MatchStarted)
    }
}
