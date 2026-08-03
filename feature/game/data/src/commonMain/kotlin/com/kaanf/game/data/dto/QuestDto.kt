package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuestDto(
    val key: String,
    val title: String,
    val description: String,
    val points: Int,
    val target: Int,
    /** Foto questlerinde etiketlenmesi gereken kişi sayısı; diğerlerinde 0. */
    val requiredTags: Int = 0,
    val progress: Int,
    val completed: Boolean,
    val claimed: Boolean,
)
