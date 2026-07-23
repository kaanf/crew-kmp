package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory
import com.kaanf.game.presentation.component.GameTaskCard
import com.kaanf.game.presentation.gamelobby.component.dialog.LeaveEventDialog
import com.kaanf.game.presentation.session.component.PhaseHeaderCard
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_loser_accepts_accept_action
import crew.feature.game.presentation.generated.resources.match_phase_loser_accepts_eyebrow
import crew.feature.game.presentation.generated.resources.match_phase_loser_accepts_loading
import crew.feature.game.presentation.generated.resources.match_phase_loser_accepts_reject_action
import crew.feature.game.presentation.generated.resources.match_phase_loser_accepts_subtitle
import crew.feature.game.presentation.generated.resources.match_phase_loser_accepts_title
import crew.feature.game.presentation.generated.resources.match_task_reject_confirm_reject
import crew.feature.game.presentation.generated.resources.match_task_reject_confirm_stay
import crew.feature.game.presentation.generated.resources.match_task_reject_confirm_subtitle
import crew.feature.game.presentation.generated.resources.match_task_reject_confirm_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.abs

@Composable
fun LoserAcceptsPhase(
    opponentName: String,
    task: GameTask,
    isResponding: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val opponentUppercase = opponentName.uppercase()
    var showRejectConfirm by remember { mutableStateOf(false) }

    if (showRejectConfirm) {
        BaseDialog(onDismissRequest = { showRejectConfirm = false }) {
            LeaveEventDialog(
                title = stringResource(Res.string.match_task_reject_confirm_title),
                subtitle = stringResource(Res.string.match_task_reject_confirm_subtitle, abs(task.rejectPoints)),
                stayLabel = stringResource(Res.string.match_task_reject_confirm_stay),
                leaveLabel = stringResource(Res.string.match_task_reject_confirm_reject),
                onStay = { showRejectConfirm = false },
                onLeave = {
                    showRejectConfirm = false
                    onReject()
                },
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PhaseHeaderCard(
            header = stringResource(Res.string.match_phase_loser_accepts_eyebrow, opponentUppercase),
            title = stringResource(Res.string.match_phase_loser_accepts_title)
        )

        GameTaskCard(
            card = task.toUiModel(),
        )

        Text(
            text = stringResource(Res.string.match_phase_loser_accepts_subtitle, opponentName),
            style = MaterialTheme.typography.bodyMedium,
            color = AccessDefaults.TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            BaseButton(
                text = stringResource(Res.string.match_phase_loser_accepts_accept_action),
                filled = true,
                enabled = !isResponding,
                isLoading = isResponding,
                loadingText = stringResource(Res.string.match_phase_loser_accepts_loading),
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = stringResource(Res.string.match_phase_loser_accepts_reject_action, task.rejectPoints),
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !isResponding,
                        onClick = { showRejectConfirm = true },
                    ),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.LeftArrowColor,
                    fontSize = 12.sp,
                ),
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    CrewTheme {
        LoserAcceptsPhase(
            opponentName = "Mira",
            task = GameTask(
                id = "1",
                title = "🌍 Get two strangers to teach you the same word in their language.",
                points = 20,
                categories = listOf(TaskCategory.Social),
            ),
            isResponding = false,
            onAccept = {},
            onReject = {},
        )
    }
}
