package com.kaanf.game.presentation.scanopponent

sealed interface ScanOpponentAction {
    data object OnCloseClicked : ScanOpponentAction
    data object OnScanResult : ScanOpponentAction
}
