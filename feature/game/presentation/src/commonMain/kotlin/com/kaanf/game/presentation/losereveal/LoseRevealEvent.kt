package com.kaanf.game.presentation.losereveal

sealed interface LoseRevealEvent {
    data object NavigateBack : LoseRevealEvent
}
