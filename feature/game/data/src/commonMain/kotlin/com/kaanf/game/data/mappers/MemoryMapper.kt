package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.EventMemoryDto
import com.kaanf.game.domain.model.EventMemory
import com.kaanf.game.domain.model.MemoryTag
import kotlin.time.Instant

fun EventMemoryDto.toDomain(): EventMemory = EventMemory(
    id = id,
    imageUrl = imageUrl,
    ownerName = ownerName,
    ownerProfilePictureUrl = ownerProfilePictureUrl,
    isMine = isMine,
    questKey = questKey,
    taskTitle = taskTitle,
    tagged = tagged.map {
        MemoryTag(
            participantId = it.participantId,
            fullName = it.fullName,
            profilePictureUrl = it.profilePictureUrl,
            pinX = it.pinX,
            pinY = it.pinY,
        )
    },
    capturedAt = Instant.parse(createdAt),
)
