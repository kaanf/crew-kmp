package com.kaanf.game.presentation.gamerpsready

sealed interface GameRpsReadyAction {
    data object OnBackClick : GameRpsReadyAction
}
