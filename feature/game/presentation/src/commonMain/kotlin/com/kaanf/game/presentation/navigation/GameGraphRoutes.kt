package com.kaanf.game.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface GameGraphRoutes {
    @Serializable
    data class Graph(val eventId: String) : GameGraphRoutes

    @Serializable
    data object GameLobby : GameGraphRoutes

    // Maç oturumu container'ı; eski faz route'ları (RpsReady, WhoWon, ...) buraya MatchPhase olarak katlandı.
    @Serializable
    data class Game(val eventId: String) : GameGraphRoutes

    @Serializable
    data object PersonalMatchQR : GameGraphRoutes

    @Serializable
    data class ScanOpponent(val eventId: String) : GameGraphRoutes

    // Graph dışında yaşar: leaderboard'a geçerken oyun graph'ı (soketiyle birlikte) pop edilir.
    @Serializable
    data class Leaderboard(val eventId: String) : GameGraphRoutes
}
