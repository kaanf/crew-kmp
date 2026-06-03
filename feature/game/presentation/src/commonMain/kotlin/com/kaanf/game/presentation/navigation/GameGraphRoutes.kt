package com.kaanf.game.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface GameGraphRoutes {
    @Serializable
    data class Graph(val eventId: String) : GameGraphRoutes

    @Serializable
    data object GameLobby : GameGraphRoutes

    @Serializable
    data class Game(val eventId: String) : GameGraphRoutes

    @Serializable
    data object PersonalMatchQR : GameGraphRoutes

    @Serializable
    data class ScanOpponent(val eventId: String) : GameGraphRoutes

    @Serializable
    data object GameRpsReady : GameGraphRoutes

    @Serializable
    data object WhoWon : GameGraphRoutes

    @Serializable
    data object GameConfirmation : GameGraphRoutes

    @Serializable
    data object WinnerPicks : GameGraphRoutes

    @Serializable
    data object WinnerWaits : GameGraphRoutes

    @Serializable
    data object LoserWaits : GameGraphRoutes

    @Serializable
    data object LoserAccepts : GameGraphRoutes

    @Serializable
    data object TaskActive : GameGraphRoutes

    @Serializable
    data object WinnerConfirms : GameGraphRoutes

    @Serializable
    data object WinReveal : GameGraphRoutes

    @Serializable
    data object LoseReveal : GameGraphRoutes
}
