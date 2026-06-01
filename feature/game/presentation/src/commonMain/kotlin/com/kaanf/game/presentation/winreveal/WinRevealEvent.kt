package com.kaanf.game.presentation.winreveal

sealed interface WinRevealEvent {
    data object NavigateBack : WinRevealEvent
}
