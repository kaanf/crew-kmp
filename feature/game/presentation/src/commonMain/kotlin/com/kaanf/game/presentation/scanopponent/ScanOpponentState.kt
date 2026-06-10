package com.kaanf.game.presentation.scanopponent

data class ScanOpponentState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showGameRequestSheet: Boolean = false,
    val opponentName: String? = null,
    val opponentPhotoUrl: String? = null,
    val selfPhotoUrl: String? = null,
)
