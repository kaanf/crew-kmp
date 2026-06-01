package com.kaanf.game.presentation.winreveal

sealed interface WinRevealAction {
    data object OnBackClick : WinRevealAction
}
