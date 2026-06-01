package com.kaanf.game.presentation.whowon

sealed interface WhoWonAction {
    data object OnBackClick : WhoWonAction
}
