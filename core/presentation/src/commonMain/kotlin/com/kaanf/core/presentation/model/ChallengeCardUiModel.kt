package com.kaanf.core.presentation.model

data class ChallengeCardUiModel(
    val variant: ChallengeCardVariant,
    val description: String,
    val points: Int,
    // İşaretli, göreve özel reject cezası; footer'ı olmayan vitrin kartları default'u kullanır.
    val rejectPoints: Int = -5,
)
