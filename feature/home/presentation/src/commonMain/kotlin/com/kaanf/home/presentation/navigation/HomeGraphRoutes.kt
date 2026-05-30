package com.kaanf.home.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface HomeGraphRoutes {
    @Serializable
    data object Graph : HomeGraphRoutes

    @Serializable
    data object Dashboard : HomeGraphRoutes

    @Serializable
    data object EventDetail : HomeGraphRoutes

    @Serializable
    data object TicketQr : HomeGraphRoutes

    @Serializable
    data object EventCode : HomeGraphRoutes

    @Serializable
    data object GameLobby : HomeGraphRoutes
}
