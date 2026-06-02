package com.kaanf.game.presentation.model

data class GameResultOptionUi(
    val title: String,
    val description: String,
    val pointText: String,
    val emoji: String,
    val isSelected: Boolean = false
)
