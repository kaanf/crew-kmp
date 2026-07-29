package com.kaanf.home.presentation.dashboard

import com.kaanf.core.presentation.model.ChallengeCardUiModel
import com.kaanf.core.presentation.model.ChallengeCardVariant
import com.kaanf.home.presentation.model.EventDashboardUiModel

data class DashboardState(
    val featuredEvents: List<EventDashboardUiModel> = emptyList(),
    val myEvents: List<EventDashboardUiModel> = emptyList(),
    val doorsOpenEvents: List<EventDashboardUiModel> = emptyList(),
    val upcomingEvents: List<EventDashboardUiModel> = emptyList(),
    val tasks: List<ChallengeCardUiModel> = listOf(
        ChallengeCardUiModel(
            description = "Get two strangers to teach you the same word in their language. Both of them, same word.",
            variant = ChallengeCardVariant.Social,
            points = 20,
        ),
        ChallengeCardUiModel(
            description = "Walk to the loudest table and convince one of them to teach you a dance move.",
            variant = ChallengeCardVariant.Bold,
            points = 35,
        ),
        ChallengeCardUiModel(
            description = "Find someone wearing your favourite colour. Ask why they chose it tonight.",
            variant = ChallengeCardVariant.Icebreaker,
            points = 10,
        ),
        ChallengeCardUiModel(
            description = "Sincerely compliment three different people on something they chose for tonight.",
            variant = ChallengeCardVariant.Flirty,
            points = 35,
        ),
        ChallengeCardUiModel(
            description = "Find one more player. Together find a fourth. Selfie. Bring me proof.",
            variant = ChallengeCardVariant.Team,
            points = 20,
        ),
    ),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val profilePictureUrl: String? = null,
    val userName: String? = null
)
