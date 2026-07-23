package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory
import com.kaanf.game.presentation.component.GameTaskCard
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_winner_confirms_completed_action
import crew.feature.game.presentation.generated.resources.match_phase_winner_confirms_eyebrow
import crew.feature.game.presentation.generated.resources.match_phase_winner_confirms_loading
import crew.feature.game.presentation.generated.resources.match_phase_winner_confirms_not_done_action
import crew.feature.game.presentation.generated.resources.match_phase_winner_confirms_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WinnerConfirmsPhase(
    opponentName: String,
    task: GameTask?,
    isConfirming: Boolean,
    onConfirm: (completed: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.match_phase_winner_confirms_eyebrow),
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp,
            ),
        )

        Text(
            text = stringResource(Res.string.match_phase_winner_confirms_title, opponentName),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        task?.let {
            GameTaskCard(
                card = it.toUiModel(),
                isExpanded = true,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BaseButton(
                text = stringResource(Res.string.match_phase_winner_confirms_not_done_action),
                filled = false,
                borderColor = AccessDefaults.LeftArrowColor,
                contentColor = AccessDefaults.LeftArrowColor,
                enabled = !isConfirming,
                onClick = { onConfirm(false) },
                modifier = Modifier
                    .weight(0.5f),
            )

            BaseButton(
                text = stringResource(Res.string.match_phase_winner_confirms_completed_action),
                filled = true,
                isLoading = isConfirming,
                loadingText = stringResource(Res.string.match_phase_winner_confirms_loading),
                onClick = { onConfirm(true) },
                modifier = Modifier
                    .weight(0.5f),
            )
        }
    }
}

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
                categories = listOf(TaskCategory.Social),
            ),
            isConfirming = false,
            onConfirm = {},
        )
    }
}
