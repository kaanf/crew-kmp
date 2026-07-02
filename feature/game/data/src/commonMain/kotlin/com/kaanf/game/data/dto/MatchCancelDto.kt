package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** `POST .../cancel` yanıtı; geçiş için kullanılmaz, gövde okunup atılır. */
@Serializable
data class MatchCancelDto(
    val matchId: String,
    val eventId: String,
    val state: String,
)
