package com.kaanf.game.presentation.session

sealed interface MatchSessionAction {
    data object OnBackClick : MatchSessionAction
    data object OnExitConfirmed : MatchSessionAction
    data object OnExitDismissed : MatchSessionAction

    // Lobi aksiyonları: lobi ekranı da aynı graph-scoped session VM'ini kullanır.
    data object OnLobbyCountdownFinished : MatchSessionAction
    data object OnEnterGameClick : MatchSessionAction
    data object OnLobbyExitConfirmed : MatchSessionAction
    data object OnScanClicked : MatchSessionAction
    data class OnScanResult(val scannedMatchQrToken: String) : MatchSessionAction

    data object OnInviteAccepted : MatchSessionAction
    data object OnInviteDeclined : MatchSessionAction

    data object OnReadyClick : MatchSessionAction

    data class OnReportResult(val won: Boolean) : MatchSessionAction

    data class OnTaskSelected(val taskId: String) : MatchSessionAction
    data object OnSendTaskClick : MatchSessionAction

    data object OnAcceptTask : MatchSessionAction
    data object OnRejectTask : MatchSessionAction

    data class OnConfirmTask(val completed: Boolean) : MatchSessionAction

    data object OnFinishMatch : MatchSessionAction
}
