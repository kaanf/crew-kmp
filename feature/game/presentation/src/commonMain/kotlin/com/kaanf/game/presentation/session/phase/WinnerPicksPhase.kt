package com.kaanf.game.presentation.session.phase

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.progressbar.ThreeDotsAnimatedCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory
import com.kaanf.game.presentation.component.GameTaskCard
import com.kaanf.game.presentation.component.taskAccentColor
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_winner_picks_description
import crew.feature.game.presentation.generated.resources.match_phase_winner_picks_eyebrow
import crew.feature.game.presentation.generated.resources.match_phase_winner_picks_loading
import crew.feature.game.presentation.generated.resources.match_phase_winner_picks_send_action
import crew.feature.game.presentation.generated.resources.match_phase_winner_picks_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WinnerPicksPhase(
    opponentName: String,
    isLoading: Boolean,
    tasks: List<GameTask>,
    selectedTaskId: String?,
    isOffering: Boolean,
    onTaskSelected: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val opponentUppercase = opponentName.uppercase()

    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ThreeDotsAnimatedCard(dotRadius = 3.dp, spacing = 6.dp)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.match_phase_winner_picks_eyebrow, opponentUppercase),
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp,
            ),
        )

        Text(
            text = stringResource(Res.string.match_phase_winner_picks_title, tasks.size),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(Res.string.match_phase_winner_picks_description, opponentName),
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        tasks.forEach { task ->
            WinnerPickTaskCard(
                task = task,
                selectedTaskId = selectedTaskId,
                onTaskSelected = onTaskSelected,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        BaseButton(
            text = stringResource(Res.string.match_phase_winner_picks_send_action, opponentName),
            filled = true,
            enabled = selectedTaskId != null && !isOffering,
            isLoading = isOffering,
            loadingText = stringResource(Res.string.match_phase_winner_picks_loading, opponentUppercase),
            onClick = onSendClick,
        )
    }
}

@Composable
private fun WinnerPickTaskCard(
    task: GameTask,
    selectedTaskId: String?,
    onTaskSelected: (String) -> Unit,
) {
    val isSelectionActive = selectedTaskId != null
    val selected = task.id == selectedTaskId
    val card = task.toUiModel()
    val accentColor = card.variant.taskAccentColor()

    val scale by animateFloatAsState(
        targetValue = when {
            selected -> 1.035f
            isSelectionActive -> 0.98f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "winner_pick_task_scale",
    )
    val dimAlpha by animateFloatAsState(
        targetValue = if (isSelectionActive && !selected) 0.48f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "winner_pick_task_dim",
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "winner_pick_task_glow",
    )
    val shadowElevation by animateFloatAsState(
        targetValue = if (selected) 18f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "winner_pick_task_shadow_elevation",
    )

    GameTaskCard(
        modifier = Modifier
            .zIndex(if (selected) 1f else 0f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.shadowElevation = shadowElevation.dp.toPx()
                shape = AccessShapes.Large
                clip = false
                ambientShadowColor = accentColor.copy(alpha = 0.48f * glowAlpha)
                spotShadowColor = accentColor.copy(alpha = 0.72f * glowAlpha)
            }
            .drawWithContent {
                drawContent()
                if (glowAlpha > 0f) {
                    val corner = 16.dp.toPx()
                    drawRoundRect(
                        color = accentColor.copy(alpha = 0.28f * glowAlpha),
                        topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                        size = Size(
                            width = size.width - 4.dp.toPx(),
                            height = size.height - 4.dp.toPx(),
                        ),
                        cornerRadius = CornerRadius(corner, corner),
                        style = Stroke(width = 8.dp.toPx()),
                    )
                    drawRoundRect(
                        color = accentColor.copy(alpha = 0.42f * glowAlpha),
                        topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
                        size = Size(
                            width = size.width - 2.dp.toPx(),
                            height = size.height - 2.dp.toPx(),
                        ),
                        cornerRadius = CornerRadius(corner, corner),
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }
                if (dimAlpha > 0f) {
                    drawRoundRect(
                        color = Color.Black.copy(alpha = dimAlpha),
                        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                    )
                }
            },
        card = card,
        selected = selected,
        onClick = { onTaskSelected(task.id) },
    )
}

@Composable
@Preview
fun WinnerPicksPhasePreview() {
    CrewTheme {
        WinnerPicksPhase(
            opponentName = "Mira",
            isLoading = false,
            tasks = sampleWinnerTasks,
            selectedTaskId = sampleWinnerTasks.first().id,
            isOffering = false,
            onTaskSelected = {},
            onSendClick = {},
        )
    }
}

private val sampleWinnerTasks = listOf(
    GameTask(
        id = "1",
        title = "🌍 Get two strangers to teach you the same word in their language.",
        categories = listOf(TaskCategory.Social),
        points = 20,
    ),
    GameTask(
        id = "2",
        title = "🕺 Walk to the loudest table and convince one of them to teach you a dance move.",
        categories = listOf(TaskCategory.Bold),
        points = 35,
    ),
    GameTask(
        id = "3",
        title = "🎨 Find someone wearing your favourite colour. Ask why they chose it tonight.",
        categories = listOf(TaskCategory.Icebreaker),
        points = 10,
    ),
)
