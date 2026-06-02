package com.kaanf.game.presentation.gamerpsready

data class GameRpsReadyState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showExitConfirmSheet: Boolean = false
)
