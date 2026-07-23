package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.EventMemoryDto
import com.kaanf.game.domain.model.EventMemory
import kotlin.time.Instant

fun EventMemoryDto.toDomain(): EventMemory = EventMemory(
    id = id,
    imageUrl = imageUrl,
    ownerName = ownerName,
    ownerProfilePictureUrl = ownerProfilePictureUrl,
    isMine = isMine,
    capturedAt = Instant.parse(createdAt),
)
