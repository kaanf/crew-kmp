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
}
