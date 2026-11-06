package com.kaanf.core.data.analytics

import com.kaanf.core.domain.analytics.Analytics
import com.kaanf.core.domain.analytics.AnalyticsEvent
import kotlin.coroutines.cancellation.CancellationException

/** Hosts install a non-blocking SDK sink only after analytics is explicitly enabled. */
object ProductAnalytics : Analytics {
    var capture: ((String, Map<String, String>) -> Unit)? = null

    override fun track(event: AnalyticsEvent) {
        val name = when (event) {
            AnalyticsEvent.AppOpened -> "app_opened"
            is AnalyticsEvent.ScreenViewed -> "screen_viewed"
            is AnalyticsEvent.TaskSelected -> "task_selected"
            AnalyticsEvent.MatchStarted -> "match_started"
            AnalyticsEvent.MatchCompleted -> "match_completed"
            AnalyticsEvent.MatchAbandoned -> "match_abandoned"
            AnalyticsEvent.QuestOpened -> "quest_opened"
        }
        val properties = when (event) {
            is AnalyticsEvent.ScreenViewed -> mapOf("screen_name" to event.screen.name.lowercase())
            is AnalyticsEvent.TaskSelected -> mapOf("task_category" to event.category.name.lowercase())
            else -> emptyMap()
        }
        // SDK failures must not change navigation or gameplay. No payload logging.
        try {
            capture?.invoke(name, properties)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            // Best effort product telemetry; never retry a user action for analytics.
        }
    }
}
