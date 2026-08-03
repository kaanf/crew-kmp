package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventParticipantDto(
    val id: String,
    val userId: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    val attendanceState: String,
)
