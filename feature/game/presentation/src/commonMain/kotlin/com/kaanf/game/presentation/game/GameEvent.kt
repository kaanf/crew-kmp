package com.kaanf.game.presentation.game

sealed interface GameEvent {
    data object NavigateToDashboard : GameEvent
    data object NavigateToScanOpponent : GameEvent
    data object NavigateToGameRpsReady : GameEvent
}
