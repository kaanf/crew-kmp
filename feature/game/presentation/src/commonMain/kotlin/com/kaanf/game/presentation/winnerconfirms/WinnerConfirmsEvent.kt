package com.kaanf.game.presentation.winnerconfirms

sealed interface WinnerConfirmsEvent {
    data object NavigateBack : WinnerConfirmsEvent
}
