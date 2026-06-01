package com.kaanf.game.presentation.gamerpsready

sealed interface GameRpsReadyEvent {
    data object NavigateBack : GameRpsReadyEvent
}
