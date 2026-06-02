package com.kaanf.game.presentation.winnerconfirms

sealed interface WinnerConfirmsAction {
    data object OnBackClick : WinnerConfirmsAction
}
