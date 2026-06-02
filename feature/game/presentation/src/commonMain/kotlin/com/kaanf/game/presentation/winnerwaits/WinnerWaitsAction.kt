package com.kaanf.game.presentation.winnerwaits

sealed interface WinnerWaitsAction {
    data object OnBackClick : WinnerWaitsAction
}
