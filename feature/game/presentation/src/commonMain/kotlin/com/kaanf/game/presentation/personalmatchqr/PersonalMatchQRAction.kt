package com.kaanf.game.presentation.personalmatchqr

sealed interface PersonalMatchQRAction {
    data object OnBackClick : PersonalMatchQRAction
}
