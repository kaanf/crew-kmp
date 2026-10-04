package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.card.GradientChallengeCard
import com.kaanf.core.designsystem.component.progressbar.ThreeDotsAnimatedCard
import com.kaanf.core.designsystem.modifier.carouselPage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_winner_picks_description
import crew.feature.game.presentation.generated.resources.match_phase_winner_picks_loading
import crew.feature.game.presentation.generated.resources.match_phase_winner_picks_send_action
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val TaskCardShape = RoundedCornerShape(28.dp)

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

    // Seçim = ortadaki kart; ayrı bir "seç" dokunuşu yok.
    val pagerState = rememberPagerState { tasks.size }
    val currentOnTaskSelected by rememberUpdatedState(onTaskSelected)
    LaunchedEffect(pagerState, tasks) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            tasks.getOrNull(page)?.let { currentOnTaskSelected(it.id) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            val sidePadding = ((maxWidth - TaskCardSize) / 2).coerceAtLeast(0.dp)

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = sidePadding),
                userScrollEnabled = !isOffering,
                modifier = Modifier.fillMaxWidth(),
            ) { page ->
                GradientChallengeCard(
                    card = tasks[page].toUiModel(),
                    cardSize = TaskCardSize,
                    emphasized = true,
                    modifier = Modifier
                        .zIndex(if (page == pagerState.currentPage) 1f else 0f)
                        .carouselPage(pagerState = pagerState, page = page, shape = TaskCardShape),
                )
            }
        }

        PagerDots(pageCount = tasks.size, currentPage = pagerState.currentPage)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.match_phase_winner_picks_description, opponentName),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextSecondary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
            )

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
}

@Composable
private fun PagerDots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = if (index == currentPage) AccessDefaults.Accent else AccessDefaults.Border,
                        shape = CircleShape,
                    ),
            )
        }
    }
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
        category = TaskCategory.Storytime,
        points = 20,
    ),
    GameTask(
        id = "2",
        title = "🕺 Walk to the loudest table and convince one of them to teach you a dance move.",
        category = TaskCategory.Bold,
        points = 35,
    ),
    GameTask(
        id = "3",
        title = "🎨 Find someone wearing your favourite colour. Ask why they chose it tonight.",
        category = TaskCategory.Icebreaker,
        points = 10,
    ),
)
