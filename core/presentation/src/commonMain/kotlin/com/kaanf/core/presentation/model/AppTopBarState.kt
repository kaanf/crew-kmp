package com.kaanf.core.presentation.model

sealed interface AppTopBarState {
    data object Login : AppTopBarState
    data object Register : AppTopBarState
    data object Dashboard : AppTopBarState
    data object EventDetail : AppTopBarState
    data object TicketQr : AppTopBarState
    data object EventCode : AppTopBarState
    data class GameLobby(val title: String) : AppTopBarState
    data object Game : AppTopBarState
    data object ScanOpponent : AppTopBarState
    data object RpsReady : AppTopBarState
    data object RpsConfirmation : AppTopBarState
    data object WinnerPicks : AppTopBarState
    data object WinnerConfirms : AppTopBarState
    data object LoserWaits : AppTopBarState
    data object LoserAccepts : AppTopBarState
    data object LoserActiveTask : AppTopBarState
}
