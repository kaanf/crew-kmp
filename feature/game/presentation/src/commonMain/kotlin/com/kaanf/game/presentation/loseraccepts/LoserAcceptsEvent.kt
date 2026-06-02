package com.kaanf.game.presentation.loseraccepts

sealed interface LoserAcceptsEvent {
    data object NavigateBack : LoserAcceptsEvent
}
