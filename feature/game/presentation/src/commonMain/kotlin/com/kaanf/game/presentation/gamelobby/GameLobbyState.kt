package com.kaanf.game.presentation.gamelobby

data class GameLobbyState(
    val targetEpochMillis: Long = 0L,
    val showGameStartSheet: Boolean = false,
    val showExitConfirmDialog: Boolean = false,
)
