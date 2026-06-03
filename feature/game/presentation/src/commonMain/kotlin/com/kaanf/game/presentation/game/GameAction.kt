package com.kaanf.game.presentation.game

sealed interface GameAction {
    data object OnBackClick : GameAction
    data object OnScanClicked : GameAction
    data object OnExitConfirmed : GameAction
    data object OnExitDismissed : GameAction

    // Gelen maç davetine (MATCH_INVITE_RECEIVED) yanıt.
    data object OnInviteAccepted : GameAction
    data object OnInviteDeclined : GameAction
}
