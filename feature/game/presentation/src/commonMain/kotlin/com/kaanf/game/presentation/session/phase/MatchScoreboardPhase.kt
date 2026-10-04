package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.progressbar.ThreeDotsAnimatedCard
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.domain.model.MatchScoreboardEntry
import com.kaanf.game.presentation.session.component.MatchScoreboardCard
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_finish_action
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_finish_loading
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MatchScoreboardPhase(
    entries: List<MatchScoreboardEntry>,
    currentUserId: String?,
    isLoading: Boolean,
    completed: Boolean,
    forfeit: Boolean,
    isFinishing: Boolean,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    // Fotolar scoreboard payload'ında yok; session state'ten gelir, yoksa initials'a düşülür.
    currentUserPhotoUrl: String? = null,
    opponentPhotoUrl: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isLoading) {
            ThreeDotsAnimatedCard()
        } else {
            // Çağıran oyuncunun kartı en üstte gösterilir.
            val ordered = entries.sortedByDescending { it.userId == currentUserId }
            ordered.forEach { entry ->
                MatchScoreboardCard(
                    entry = entry,
                    isYou = entry.userId == currentUserId,
                    taskCompleted = completed,
                    forfeit = forfeit,
                    photoUrl = if (entry.userId == currentUserId) currentUserPhotoUrl else opponentPhotoUrl,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        BaseButton(
            text = stringResource(Res.string.match_phase_scoreboard_finish_action),
            filled = true,
            isLoading = isFinishing,
            loadingText = stringResource(Res.string.match_phase_scoreboard_finish_loading),
            enabled = !isLoading && !isFinishing,
            onClick = onFinish,
        )
    }
}

@Composable
@Preview
private fun Preview() {
    CrewTheme {
        MatchScoreboardPhase(
            entries = listOf(
                MatchScoreboardEntry(
                    participantId = "p1",
                    userId = "u1",
                    fullName = "Kaan",
                    isWinner = true,
                    points = 30,
                ),
                MatchScoreboardEntry(
                    participantId = "p2",
                    userId = "u2",
                    fullName = "Mira",
                    isWinner = false,
                    points = 20,
                ),
            ),
            currentUserId = "u1",
            isLoading = false,
            completed = true,
            forfeit = false,
            isFinishing = false,
            onFinish = {},
        )
    }
}
