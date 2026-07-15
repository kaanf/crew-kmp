package com.kaanf.game.presentation.session

sealed interface MatchSessionEvent {
    data object NavigateToScanOpponent : MatchSessionEvent
    data object NavigateToDashboard : MatchSessionEvent

    /** Oyun süresi doldu (gameEndsAt geçti): etkinlik leaderboard'una geç. */
    data object NavigateToLeaderboard : MatchSessionEvent

    // Lobi ekranının tükettiği event'ler.
    data object NavigateToGame : MatchSessionEvent
    data object NavigateBack : MatchSessionEvent
}
