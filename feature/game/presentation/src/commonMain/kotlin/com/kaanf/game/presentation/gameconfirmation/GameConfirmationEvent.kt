package com.kaanf.game.presentation.gameconfirmation

sealed interface GameConfirmationEvent {
    data object NavigateBack : GameConfirmationEvent
}
