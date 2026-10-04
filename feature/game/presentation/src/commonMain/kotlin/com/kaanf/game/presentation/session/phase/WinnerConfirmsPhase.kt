package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kaanf.game.domain.model.EventMemory
import com.kaanf.game.presentation.memories.MemoryLightbox
import com.kaanf.game.presentation.session.component.TaskPhotoStatusChip
import crew.feature.game.presentation.generated.resources.match_task_photo_received_label
import crew.feature.game.presentation.generated.resources.match_task_photo_sent_label
import crew.feature.game.presentation.generated.resources.match_task_photo_waiting_label
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory
import com.kaanf.core.designsystem.component.card.GradientChallengeCard
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_winner_confirms_completed_action
import crew.feature.game.presentation.generated.resources.match_phase_winner_confirms_not_done_action
import crew.feature.game.presentation.generated.resources.match_phase_winner_confirms_subtitle
import crew.feature.game.presentation.generated.resources.match_phase_winner_confirms_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WinnerConfirmsPhase(
    opponentName: String,
    task: GameTask?,
    isConfirming: Boolean,
    onConfirm: (completed: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    opponentImageUrl: String? = null,
    photoUploaded: Boolean = false,
    photo: EventMemory? = null,
) {
    var showPhoto by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OpponentStatusCard(
            title = stringResource(Res.string.match_phase_winner_confirms_title, opponentName),
            subtitle = stringResource(Res.string.match_phase_winner_confirms_subtitle),
            opponentName = opponentName,
            opponentImageUrl = opponentImageUrl,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            task?.let {
                val tilt = remember(it.id) { tableTiltFor(it.id) }
                GradientChallengeCard(
                    card = it.toUiModel(),
                    cardSize = TaskCardSize,
                    emphasized = true,
                    modifier = Modifier.rotate(tilt),
                )
            }
        }

        if (task?.category == TaskCategory.Photo) {
            when {
                photo != null -> TaskPhotoStatusChip(
                    text = stringResource(Res.string.match_task_photo_received_label),
                    highlighted = true,
                    onClick = { showPhoto = true },
                )

                photoUploaded -> TaskPhotoStatusChip(
                    text = stringResource(Res.string.match_task_photo_sent_label),
                    highlighted = true,
                )

                else -> TaskPhotoStatusChip(text = stringResource(Res.string.match_task_photo_waiting_label))
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedIconButton(
                onClick = { onConfirm(false) },
                enabled = !isConfirming,
                border = BorderStroke(1.5.dp, AccessDefaults.LeftArrowColor),
                modifier = Modifier.size(RoundButtonSize),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(Res.string.match_phase_winner_confirms_not_done_action),
                    tint = AccessDefaults.LeftArrowColor,
                    modifier = Modifier.size(RoundIconSize),
                )
            }

            FilledIconButton(
                onClick = { onConfirm(true) },
                enabled = !isConfirming,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = AccessDefaults.Accent,
                    contentColor = AccessDefaults.OnAccent,
                    disabledContainerColor = AccessDefaults.Accent.copy(alpha = 0.5f),
                    disabledContentColor = AccessDefaults.OnAccent,
                ),
                modifier = Modifier.size(RoundButtonSize),
            ) {
                if (isConfirming) {
                    CircularProgressIndicator(
                        color = AccessDefaults.OnAccent,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(RoundIconSize),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = stringResource(Res.string.match_phase_winner_confirms_completed_action),
                        modifier = Modifier.size(RoundIconSize),
                    )
                }
            }
        }
    }

    if (showPhoto && photo != null) {
        MemoryLightbox(memory = photo, onDismiss = { showPhoto = false })
    }
}

private val RoundButtonSize = 64.dp
private val RoundIconSize = 28.dp

@Composable
@Preview
private fun Preview() {
    CrewTheme {
        WinnerConfirmsPhase(
            opponentName = "Mira",
            task = GameTask(
                id = "1",
                title = "🌍 Get two strangers to teach you the same word in their language.",
                points = 20,
                category = TaskCategory.Icebreaker,
            ),
            isConfirming = false,
            onConfirm = {},
        )
    }
}
