package com.kaanf.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckInRequestDto(
    val entryCode: String,
)

@Serializable
data class CheckInResultDto(
    val participantId: String,
    val eventId: String,
    val userId: String,
    val fullName: String,
    val attendanceState: String,
)
