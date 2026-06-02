package com.kaanf.game.presentation.winnerpicks

sealed interface WinnerPicksEvent {
    data object NavigateBack : WinnerPicksEvent
}
