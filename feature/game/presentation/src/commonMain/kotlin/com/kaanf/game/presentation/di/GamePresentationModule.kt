package com.kaanf.game.presentation.di

import com.kaanf.game.presentation.game.GameViewModel
import com.kaanf.game.presentation.gameconfirmation.GameConfirmationViewModel
import com.kaanf.game.presentation.gamerpsready.GameRpsReadyViewModel
import com.kaanf.game.presentation.gamelobby.GameLobbyViewModel
import com.kaanf.game.presentation.losereveal.LoseRevealViewModel
import com.kaanf.game.presentation.personalmatchqr.PersonalMatchQRViewModel
import com.kaanf.game.presentation.scanopponent.ScanOpponentViewModel
import com.kaanf.game.presentation.whowon.WhoWonViewModel
import com.kaanf.game.presentation.winreveal.WinRevealViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val gamePresentationModule = module {
    viewModelOf(::GameLobbyViewModel)
    viewModelOf(::GameViewModel)
    viewModelOf(::PersonalMatchQRViewModel)
    viewModelOf(::ScanOpponentViewModel)
    viewModelOf(::GameRpsReadyViewModel)
    viewModelOf(::WhoWonViewModel)
    viewModelOf(::GameConfirmationViewModel)
    viewModelOf(::WinRevealViewModel)
    viewModelOf(::LoseRevealViewModel)
}
