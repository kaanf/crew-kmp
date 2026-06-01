package com.kaanf.game.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface GameGraphRoutes {
    @Serializable
    data object Graph : GameGraphRoutes

    @Serializable
    data object GameLobby : GameGraphRoutes

    @Serializable
    data object Game : GameGraphRoutes

    @Serializable
    data object PersonalMatchQR : GameGraphRoutes

    @Serializable
    data object ScanOpponent : GameGraphRoutes

    @Serializable
    data object GameRpsReady : GameGraphRoutes

    @Serializable
    data object WhoWon : GameGraphRoutes

    @Serializable
    data object GameConfirmation : GameGraphRoutes

    @Serializable
    data object WinReveal : GameGraphRoutes

    @Serializable
    data object LoseReveal : GameGraphRoutes
}
