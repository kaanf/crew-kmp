package com.kaanf.home.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface HomeGraphRoutes {
    @Serializable
    data object Graph : HomeGraphRoutes

    @Serializable
    data object Dashboard : HomeGraphRoutes

    @Serializable
    data class EventDetail(val eventId: String) : HomeGraphRoutes

    @Serializable
    data class TicketQr(
        val eventId: String,
    ) : HomeGraphRoutes
}
