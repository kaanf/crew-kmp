package com.kaanf.game.presentation.session.phase

import com.kaanf.core.presentation.model.ChallengeCardUiModel
import com.kaanf.core.presentation.model.ChallengeCardVariant
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory

internal fun GameTask.toUiModel(): ChallengeCardUiModel = ChallengeCardUiModel(
    description = title,
    variant = categories.firstOrNull().toVariant(),
    points = points,
    rejectPoints = rejectPoints,
)

internal fun TaskCategory?.toVariant(): ChallengeCardVariant = when (this) {
    TaskCategory.Icebreaker -> ChallengeCardVariant.Icebreaker
    TaskCategory.Social -> ChallengeCardVariant.Social
    TaskCategory.Bold -> ChallengeCardVariant.Bold
    TaskCategory.FinalRound -> ChallengeCardVariant.FinalRound
    TaskCategory.Unknown, null -> ChallengeCardVariant.Social
}
