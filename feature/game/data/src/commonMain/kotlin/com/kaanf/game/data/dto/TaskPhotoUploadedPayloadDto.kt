package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskPhotoUploadedPayloadDto(
    val matchId: String,
    val eventId: String,
    val memory: EventMemoryDto,
)
