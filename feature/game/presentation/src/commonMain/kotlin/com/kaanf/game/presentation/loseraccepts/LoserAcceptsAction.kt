package com.kaanf.game.presentation.loseraccepts

sealed interface LoserAcceptsAction {
    data object OnBackClick : LoserAcceptsAction
}
