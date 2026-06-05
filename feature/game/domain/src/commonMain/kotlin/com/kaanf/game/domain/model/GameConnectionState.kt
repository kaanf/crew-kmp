package com.kaanf.game.domain.model

sealed interface GameConnectionState {
    data object Connecting : GameConnectionState
    data object Connected : GameConnectionState
    data object Reconnecting : GameConnectionState
    data class Disconnected(val code: Int?, val reason: String?) : GameConnectionState
}
