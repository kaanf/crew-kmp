package com.kaanf.game.presentation.gamerpsready

import com.kaanf.game.presentation.gamelobby.GameLobbyAction

sealed interface GameRpsReadyAction {
    data object OnBackClick : GameRpsReadyAction
    data object OnExitConfirmed : GameRpsReadyAction
    data object OnExitDismissed : GameRpsReadyAction
}
