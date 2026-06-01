package com.kaanf.game.presentation.losereveal

sealed interface LoseRevealAction {
    data object OnBackClick : LoseRevealAction
}
