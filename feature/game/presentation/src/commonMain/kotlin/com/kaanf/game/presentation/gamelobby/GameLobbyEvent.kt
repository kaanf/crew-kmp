package com.kaanf.game.presentation.gamelobby

sealed interface GameLobbyEvent {
    data object NavigateBack : GameLobbyEvent
    data object NavigateToGame : GameLobbyEvent
}
