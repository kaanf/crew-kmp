package com.kaanf.game.presentation.scanopponent

sealed interface ScanOpponentAction {
    data object OnCloseClicked : ScanOpponentAction

    // Okunan QR'ın çözümlenmiş metni = karşı tarafın maç token'ı.
    data class OnScanResult(val scannedMatchQrToken: String) : ScanOpponentAction
}
