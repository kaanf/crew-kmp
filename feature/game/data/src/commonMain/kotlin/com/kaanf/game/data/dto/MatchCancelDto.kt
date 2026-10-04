package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** `POST .../cancel` yanıtı; `state` forfeit (Cancelled) ile vazgeçmeyi (Rejected) ayırır. */
@Serializable
data class MatchCancelDto(
    val matchId: String,
    val eventId: String,
    val state: String,
)
