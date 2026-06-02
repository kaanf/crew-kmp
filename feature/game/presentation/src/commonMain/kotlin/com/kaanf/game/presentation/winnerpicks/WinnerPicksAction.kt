package com.kaanf.game.presentation.winnerpicks

sealed interface WinnerPicksAction {
    data object OnBackClick : WinnerPicksAction
}
