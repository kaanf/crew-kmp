package com.kaanf.game.presentation.gameconfirmation

sealed interface GameConfirmationAction {
    data object OnBackClick : GameConfirmationAction
}
