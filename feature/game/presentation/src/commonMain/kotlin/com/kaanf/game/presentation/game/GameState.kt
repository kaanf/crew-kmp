package com.kaanf.game.presentation.game

data class GameState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showExitConfirmDialog: Boolean = false,
)
