package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.TaskDto
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory

fun TaskDto.toDomain(): GameTask = GameTask(
    id = id,
    title = title,
    points = points,
    rejectPoints = rejectPoints,
    category = category.toTaskCategory(),
)

internal fun String.toTaskCategory(): TaskCategory = when (uppercase()) {
    "ICEBREAKER" -> TaskCategory.Icebreaker
    "TEAM" -> TaskCategory.Team
    "STORYTIME" -> TaskCategory.Storytime
    "CHALLENGE" -> TaskCategory.Challenge
    "PHOTO" -> TaskCategory.Photo
    "BOLD" -> TaskCategory.Bold
    "CONFESSION" -> TaskCategory.Confession
    "FLIRTY" -> TaskCategory.Flirty
    else -> TaskCategory.Unknown
}
