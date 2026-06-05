package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** `POST .../task/offer` yanıtı. Geçiş soketle sürüldüğü için yalnızca onay amaçlı okunur. */
@Serializable
data class MatchTaskOfferDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val taskId: String,
)
