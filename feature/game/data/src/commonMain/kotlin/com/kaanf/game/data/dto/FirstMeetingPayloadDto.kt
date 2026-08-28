package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

/** FIRST_MEETING push'u: karşı tarafın kartı + pasaporttan claim edilebilecek puan. */
@Serializable
data class FirstMeetingPayloadDto(
    val eventId: String,
    val userId: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val title: TitleDto? = null,
    val pointsAwarded: Int,
    val totalScore: Int,
)
