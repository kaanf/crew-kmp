package com.kaanf.game.presentation.session

sealed interface MatchSessionEvent {
    data object NavigateToScanOpponent : MatchSessionEvent
    data object NavigateToDashboard : MatchSessionEvent

    // Lobi ekranının tükettiği event'ler.
    data object NavigateToGame : MatchSessionEvent
    data object NavigateBack : MatchSessionEvent
}
