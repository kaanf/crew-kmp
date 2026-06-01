package com.kaanf.game.presentation.game

sealed interface GameAction {
    data object OnBackClick : GameAction
    data object OnScanClicked : GameAction
    data object OnExitConfirmed : GameAction
    data object OnExitDismissed : GameAction
}
