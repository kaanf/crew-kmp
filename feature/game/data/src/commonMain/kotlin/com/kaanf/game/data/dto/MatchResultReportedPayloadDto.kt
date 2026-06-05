package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** MATCH_RESULT_REPORTED push'u; ilk taraf sonucu bildirdiğinde rakibine gider. */
@Serializable
data class MatchResultReportedPayloadDto(
    val matchId: String,
    val eventId: String,
    val state: String,
    val reporterUserId: String,
    val claimedWinnerUserId: String,
)
