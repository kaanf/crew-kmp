package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.progressbar.ThreeDotsAnimatedCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.game.domain.model.MatchScoreboardEntry
import com.kaanf.game.presentation.session.component.MatchScoreboardCard
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_finish_action
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_finish_loading
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_subtitle_loading
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_subtitle_loser_done
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_subtitle_loser_forfeit
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_subtitle_loser_not_done
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_subtitle_winner_bailed
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_subtitle_winner_done
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_subtitle_winner_forfeit
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_title_highlight
import crew.feature.game.presentation.generated.resources.match_phase_scoreboard_title_prefix
import org.jetbrains.compose.resources.stringResource

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
) {
    val myEntry = entries.firstOrNull { it.userId == currentUserId }
    val titlePrefix = stringResource(Res.string.match_phase_scoreboard_title_prefix)
    val titleHighlight = stringResource(Res.string.match_phase_scoreboard_title_highlight)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = AccessDefaults.TextPrimary)) {
                    append(titlePrefix)
                }
                withStyle(
                    style = SpanStyle(
                        color = AccessDefaults.Accent,
                        fontWeight = FontWeight.Bold,
                    ),
                ) {
                    append(titleHighlight)
                }
            },
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            text = scoreboardSubtitle(myEntry = myEntry, completed = completed, forfeit = forfeit),
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

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
                )
            }
        }

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
private fun scoreboardSubtitle(
    myEntry: MatchScoreboardEntry?,
    completed: Boolean,
    forfeit: Boolean,
): String = when {
    myEntry == null -> stringResource(Res.string.match_phase_scoreboard_subtitle_loading)
    // Forfeit: ayrılan her zaman kaybeden, kalan her zaman kazanan.
    forfeit && myEntry.isWinner ->
        stringResource(Res.string.match_phase_scoreboard_subtitle_winner_forfeit)
    forfeit -> stringResource(Res.string.match_phase_scoreboard_subtitle_loser_forfeit)
    myEntry.isWinner && completed -> stringResource(
        Res.string.match_phase_scoreboard_subtitle_winner_done,
    )
    myEntry.isWinner -> stringResource(Res.string.match_phase_scoreboard_subtitle_winner_bailed)
    completed -> stringResource(Res.string.match_phase_scoreboard_subtitle_loser_done)
    else -> stringResource(Res.string.match_phase_scoreboard_subtitle_loser_not_done)
}
