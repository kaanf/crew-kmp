package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MyParticipantDto(
    val id: String,
    val eventId: String,
    val userId: String,
    val attendanceState: String,
    val matchState: String? = null,
    val score: Int,
    val winCount: Int = 0,
    val matchesCount: Int = 0,
    val matchQrToken: String,
)
