package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** `POST .../ready` yanıtı. `bothReady` sunucu tarafı bilgisidir; geçiş soketle sürülür. */
@Serializable
data class MatchReadyDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val bothReady: Boolean,
)
