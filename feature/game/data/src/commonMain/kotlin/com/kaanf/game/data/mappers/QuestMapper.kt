package com.kaanf.game.data.mappers

import com.kaanf.game.data.dto.QuestDto
import com.kaanf.game.domain.model.Quest

fun QuestDto.toDomain(): Quest = Quest(
    key = key,
    title = title,
    description = description,
    points = points,
    target = target,
    progress = progress,
    completed = completed,
    claimed = claimed,
)
