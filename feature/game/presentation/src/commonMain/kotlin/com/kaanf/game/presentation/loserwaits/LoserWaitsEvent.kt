package com.kaanf.game.presentation.loserwaits

sealed interface LoserWaitsEvent {
    data object NavigateBack : LoserWaitsEvent
}
