package com.kaanf.game.presentation.whowon

sealed interface WhoWonEvent {
    data object NavigateBack : WhoWonEvent
}
