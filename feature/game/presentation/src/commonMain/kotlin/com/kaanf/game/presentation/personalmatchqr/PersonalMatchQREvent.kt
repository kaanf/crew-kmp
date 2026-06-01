package com.kaanf.game.presentation.personalmatchqr

sealed interface PersonalMatchQREvent {
    data object NavigateBack : PersonalMatchQREvent
}
