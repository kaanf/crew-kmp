package com.kaanf.game.presentation.gameconfirmation

import com.kaanf.game.presentation.whowon.model.OpponentClaimType

data class GameConfirmationState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val claimType: OpponentClaimType = OpponentClaimType.Win
)
