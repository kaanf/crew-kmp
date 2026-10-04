package com.kaanf.game.presentation.session

import androidx.compose.ui.graphics.ImageBitmap

sealed interface MatchSessionAction {
    data object OnBackClick : MatchSessionAction
    data object OnExitConfirmed : MatchSessionAction
    data object OnExitDismissed : MatchSessionAction

    // Lobi aksiyonları: lobi ekranı da aynı graph-scoped session VM'ini kullanır.
    data object OnLobbyCountdownFinished : MatchSessionAction
    data object OnEnterGameClick : MatchSessionAction
    data object OnLobbyExitConfirmed : MatchSessionAction
    data object OnScanClicked : MatchSessionAction

    // Ekran yeniden compose olduğunda stats'ı tazeler: quest claim / scan gibi
    // başka destination'larda artan skor VM hayatta kaldığı için bayat kalıyordu.
    data object OnStatsRefreshRequested : MatchSessionAction
    data class OnScanResult(val scannedMatchQrToken: String) : MatchSessionAction

    data object OnInviteAccepted : MatchSessionAction
    data object OnInviteDeclined : MatchSessionAction

    data object OnReadyClick : MatchSessionAction

    data class OnReportResult(val won: Boolean) : MatchSessionAction

    data class OnTaskSelected(val taskId: String) : MatchSessionAction
    data object OnSendTaskClick : MatchSessionAction

    data object OnRejectTask : MatchSessionAction
    data object OnRejectTaskDismissed : MatchSessionAction
    data object OnRejectTaskConfirmed : MatchSessionAction

    data class OnTaskPhotoCaptured(val image: ImageBitmap) : MatchSessionAction

    /** [skipPhotoCheck]: kazanan "fotoğraf yok" uyarısını görüp yine de onayladı. */
    data class OnConfirmTask(
        val completed: Boolean,
        val skipPhotoCheck: Boolean = false,
    ) : MatchSessionAction
    data object OnNoPhotoWarningDismissed : MatchSessionAction

    data object OnFinishMatch : MatchSessionAction

    data object OnAnnouncementClicked : MatchSessionAction
    data object OnAnnouncementDismissed : MatchSessionAction
}
