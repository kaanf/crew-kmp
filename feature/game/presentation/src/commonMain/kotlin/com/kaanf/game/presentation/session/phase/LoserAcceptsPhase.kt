package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory
import com.kaanf.game.presentation.component.GameTaskCard
import com.kaanf.game.presentation.session.component.PhaseHeaderCard
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_loser_accepts_accept_action
import crew.feature.game.presentation.generated.resources.match_phase_loser_accepts_eyebrow
import crew.feature.game.presentation.generated.resources.match_phase_loser_accepts_loading
import crew.feature.game.presentation.generated.resources.match_phase_loser_accepts_reject_action
import crew.feature.game.presentation.generated.resources.match_phase_loser_accepts_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
            isExpanded = true,
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BaseButton(
                text = stringResource(Res.string.match_phase_loser_accepts_reject_action),
                filled = false,
                enabled = !isResponding,
                onClick = onReject,
                modifier = Modifier.weight(0.5f),
            )

            BaseButton(
                text = stringResource(Res.string.match_phase_loser_accepts_accept_action),
                filled = true,
                enabled = !isResponding,
                isLoading = isResponding,
                loadingText = stringResource(Res.string.match_phase_loser_accepts_loading),
                onClick = onAccept,
                modifier = Modifier.weight(0.5f),
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
