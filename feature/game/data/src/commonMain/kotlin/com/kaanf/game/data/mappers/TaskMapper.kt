package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.TaskDto
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory

fun TaskDto.toDomain(): GameTask = GameTask(
    id = id,
    title = title,
    points = points,
    rejectPoints = rejectPoints,
    categories = categories.map { it.toTaskCategory() },
)

internal fun String.toTaskCategory(): TaskCategory = when (uppercase()) {
    "ICEBREAKER" -> TaskCategory.Icebreaker
    "SOCIAL" -> TaskCategory.Social
    "BOLD" -> TaskCategory.Bold
    "FINAL_ROUND" -> TaskCategory.FinalRound
    else -> TaskCategory.Unknown
}
