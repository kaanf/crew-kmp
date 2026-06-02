package com.kaanf.game.presentation.loserwaits

sealed interface LoserWaitsAction {
    data object OnBackClick : LoserWaitsAction
}
