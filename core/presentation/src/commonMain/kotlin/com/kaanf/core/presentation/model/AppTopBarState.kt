package com.kaanf.core.presentation.model

sealed interface AppTopBarState {
    data object Login : AppTopBarState
    data object Register : AppTopBarState
    data object ProfilePicture : AppTopBarState
    data object ImageCrop : AppTopBarState
    data class Dashboard(val profileImageUrl: String?, val userName: String? = null) : AppTopBarState
    data class Profile(
        val hasUnsavedChanges: Boolean = false,
        val isSaving: Boolean = false,
    ) : AppTopBarState
    data object SignInMethods : AppTopBarState
    data object EventDetail : AppTopBarState
    data object TicketQr : AppTopBarState
    data object EventCode : AppTopBarState
    data class GameLobby(val title: String) : AppTopBarState
    data class Game(
        val title: String? = null,
        /** Solda quest ikonunu gösterir (yalnız oyun ana ekranında). */
        val showQuestsAction: Boolean = false,
        /** Quest ikonunun yanında damga pasaportu (adres defteri) ikonunu gösterir. */
        val showPassportAction: Boolean = false,
        /** Claim edilecek quest var: 🎯 ikonuna kırmızı nokta. */
        val questsBadge: Boolean = false,
        /** Claim edilecek tanışma puanı var: 📘 ikonuna kırmızı nokta. */
        val passportBadge: Boolean = false,
    ) : AppTopBarState
    data object ScanOpponent : AppTopBarState
    data object RpsReady : AppTopBarState
    data object RpsConfirmation : AppTopBarState
    data object WinnerPicks : AppTopBarState
    data object WinnerConfirms : AppTopBarState
    data object LoserWaits : AppTopBarState
    data object LoserAccepts : AppTopBarState
    data object LoserActiveTask : AppTopBarState
}
