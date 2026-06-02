package com.kaanf.game.presentation.winnerwaits

sealed interface WinnerWaitsEvent {
    data object NavigateBack : WinnerWaitsEvent
}
