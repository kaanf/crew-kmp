package com.kaanf.core.domain.analytics

interface Analytics {
    fun track(event: AnalyticsEvent)
}

enum class AnalyticsScreen { Dashboard, Profile, ProfileBoard, EventDetail, TicketQr, Game, Quests, Passport }
enum class AnalyticsCategory { Icebreaker, Team, Storytime, Challenge, Photo, Bold, Confession, Flirty, Unknown }

sealed interface AnalyticsEvent {
    data object AppOpened : AnalyticsEvent
    data class ScreenViewed(val screen: AnalyticsScreen) : AnalyticsEvent
    data class TaskSelected(val category: AnalyticsCategory) : AnalyticsEvent
    data object MatchStarted : AnalyticsEvent
    data object MatchCompleted : AnalyticsEvent
    data object MatchAbandoned : AnalyticsEvent
    data object QuestOpened : AnalyticsEvent
}
