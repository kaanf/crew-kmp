package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** `POST .../result` yanıtı. `winnerUserId` ancak iki taraf da hemfikir olunca dolar. */
@Serializable
data class MatchResultDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val winnerUserId: String? = null,
)
