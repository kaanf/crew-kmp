package com.kaanf.game.presentation.scanopponent

sealed interface ScanOpponentEvent {
    data object CloseScreen : ScanOpponentEvent
    data object NavigateToGameRpsReady : ScanOpponentEvent
}
