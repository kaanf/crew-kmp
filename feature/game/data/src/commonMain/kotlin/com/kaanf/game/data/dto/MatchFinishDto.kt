package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** `POST .../finish` yanıtı; geçiş için kullanılmaz, gövde okunup atılır. */
@Serializable
data class MatchFinishDto(
    val matchId: String,
    val eventId: String,
    val matchState: String? = null,
)
