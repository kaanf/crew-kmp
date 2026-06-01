package com.kaanf.game.presentation.gamelobby

sealed interface GameLobbyAction {
    data object OnBackClick : GameLobbyAction
    data object OnCountdownFinished : GameLobbyAction
    data object OnExitConfirmed : GameLobbyAction
    data object OnExitDismissed : GameLobbyAction
    data object OnEnterGameClick : GameLobbyAction
}
