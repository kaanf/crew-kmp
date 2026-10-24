package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.card.ChallengeCardTallAspectRatio
import com.kaanf.core.designsystem.component.card.GradientChallengeCard
import com.kaanf.core.presentation.model.ChallengeCardUiModel
import com.kaanf.core.presentation.model.ChallengeCardVariant
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory

/** Oyun ekranlarındaki görev kartının genişliği (seçim, aktif görev, onay). */
internal val TaskCardWidth = 280.dp

/** Oyun ekranlarının dikey görev kartı; dar ekranda kalan yüksekliğe sığacak kadar daralıyor. */
@Composable
internal fun TaskCard(
    card: ChallengeCardUiModel,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        GradientChallengeCard(
            card = card,
            width = minOf(TaskCardWidth, maxHeight * ChallengeCardTallAspectRatio),
            emphasized = true,
            tall = true,
        )
    }
}

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
