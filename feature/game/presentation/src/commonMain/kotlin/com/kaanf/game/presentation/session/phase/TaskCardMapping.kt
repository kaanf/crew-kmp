package com.kaanf.game.presentation.session.phase

import com.kaanf.core.presentation.model.ChallengeCardUiModel
import com.kaanf.core.presentation.model.ChallengeCardVariant
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory

internal fun GameTask.toUiModel(): ChallengeCardUiModel = ChallengeCardUiModel(
    description = title,
    variant = category.toVariant(),
    points = points,
    rejectPoints = rejectPoints,
)

internal fun TaskCategory?.toVariant(): ChallengeCardVariant = when (this) {
    TaskCategory.Icebreaker -> ChallengeCardVariant.Icebreaker
    TaskCategory.Team -> ChallengeCardVariant.Team
    TaskCategory.Storytime -> ChallengeCardVariant.Storytime
    TaskCategory.Challenge -> ChallengeCardVariant.Challenge
    TaskCategory.Photo -> ChallengeCardVariant.Photo
    TaskCategory.Bold -> ChallengeCardVariant.Bold
    TaskCategory.Confession -> ChallengeCardVariant.Confession
    TaskCategory.Flirty -> ChallengeCardVariant.Flirty
    TaskCategory.Unknown, null -> ChallengeCardVariant.Icebreaker
}
