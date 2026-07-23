package com.kaanf.game.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventMemoryDto(
    val id: String,
    /** Kısa ömürlü imzalı URL; [expiresAt] geçince liste yeniden çekilmeli. */
    val imageUrl: String,
    val expiresAt: String,
    val ownerParticipantId: String,
    val ownerName: String,
    val ownerProfilePictureUrl: String? = null,
    val isMine: Boolean,
    val createdAt: String,
)
