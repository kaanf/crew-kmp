package com.kaanf.game.presentation.winnerpicks

import com.kaanf.core.presentation.model.ChallengeCardUiModel
import com.kaanf.core.presentation.model.ChallengeCardVariant

data class WinnerPicksState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val tasks: List<ChallengeCardUiModel> = listOf(
        ChallengeCardUiModel(
            description = "\uD83C\uDF0D Get two strangers to teach you the same word in their language. Both of them, same word.",
            variant = ChallengeCardVariant.Social,
            points = 20,
        ),
        ChallengeCardUiModel(
            description = "\uD83D\uDD7A Walk to the loudest table and convince one of them to teach you a dance move.",
            variant = ChallengeCardVariant.Bold,
            points = 35,
        ),
        ChallengeCardUiModel(
            description = "\uD83C\uDFA8 Find someone wearing your favourite colour. Ask why they chose it tonight.",
            variant = ChallengeCardVariant.Icebreaker,
            points = 10,
        ),
    )
)
