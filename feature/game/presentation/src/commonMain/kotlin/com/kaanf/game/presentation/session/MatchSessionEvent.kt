package com.kaanf.game.presentation.session

sealed interface MatchSessionEvent {
    data object NavigateToScanOpponent : MatchSessionEvent
    data object NavigateToDashboard : MatchSessionEvent
}
