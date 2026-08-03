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
    /** Ait olduğu foto questi; serbest çekim döneminden kalan fotoğraflarda null. */
    val questKey: String? = null,
    val tagged: List<MemoryTagDto> = emptyList(),
    val createdAt: String,
)

@Serializable
data class MemoryTagDto(
    val participantId: String,
    val fullName: String,
    val profilePictureUrl: String? = null,
    /** Sol üstten itibaren 0-1 oranı; piksele çevirmek istemcinin işi. */
    val pinX: Float,
    val pinY: Float,
)

/** Yükleme isteğinin `tags` alanı: bu listenin JSON'u tek bir form alanı olarak gider. */
@Serializable
data class QuestPhotoTagRequest(
    val participantId: String,
    val pinX: Float,
    val pinY: Float,
)
