package com.kaanf.game.presentation.quests

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.FullScreenLoader
import com.kaanf.core.designsystem.component.progressbar.BaseProgressBar
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.game.domain.model.Quest
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.quest_badge_first_opponent
import crew.feature.game.presentation.generated.resources.quest_badge_meet_10
import crew.feature.game.presentation.generated.resources.quest_badge_meet_15
import crew.feature.game.presentation.generated.resources.quest_badge_meet_5
import crew.feature.game.presentation.generated.resources.quest_badge_points_earned
import crew.feature.game.presentation.generated.resources.quest_badge_rematch_win
import crew.feature.game.presentation.generated.resources.quest_badge_win_streak_3
import crew.feature.game.presentation.generated.resources.quest_badge_win_streak_5
import crew.feature.game.presentation.generated.resources.quests_claim_format
import crew.feature.game.presentation.generated.resources.quests_claimed_label
import crew.feature.game.presentation.generated.resources.quests_close
import crew.feature.game.presentation.generated.resources.quests_collecting
import crew.feature.game.presentation.generated.resources.quests_empty
import crew.feature.game.presentation.generated.resources.quests_family_beat
import crew.feature.game.presentation.generated.resources.quests_family_meet
import crew.feature.game.presentation.generated.resources.quests_family_rematch
import crew.feature.game.presentation.generated.resources.quests_family_revenge
import crew.feature.game.presentation.generated.resources.quests_family_streak
import crew.feature.game.presentation.generated.resources.quests_level_progress
import crew.feature.game.presentation.generated.resources.quests_top_bar_title
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun QuestsRoot(onBack: () -> Unit, viewModel: QuestsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.refresh() }
    QuestsScreen(state = state, onBack = onBack, onClaim = viewModel::claim)
}

@Composable
fun QuestsScreen(
    state: QuestsState,
    onBack: () -> Unit,
    onClaim: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groups = remember(state.quests) { groupQuests(state.quests) }
    var selectedQuestKey by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedGroup = groups.firstOrNull { group -> group.levels.any { it.key == selectedQuestKey } }
    val selectedQuest = selectedGroup?.levels?.firstOrNull { it.key == selectedQuestKey }
    val listState = rememberLazyListState()

    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                state = AppTopBarState.GameLobby(stringResource(Res.string.quests_top_bar_title)),
                onBackClick = onBack,
            )
        },
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).consumeWindowInsets(innerPadding)) {
            if (state.isLoading) {
                FullScreenLoader(Modifier.weight(1f))
            } else {
                if (groups.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(Res.string.quests_empty), style = MaterialTheme.typography.bodyMedium,
                            color = AccessDefaults.TextSecondary, textAlign = TextAlign.Center)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(28.dp),
                    ) {
                        items(groups, key = { it.key }) { group ->
                            QuestMapSection(
                                group = group,
                                selectedQuestKey = selectedQuestKey,
                                claimingKey = state.claimingKey,
                                onClaim = onClaim,
                                onSelect = { selectedQuestKey = it.key },
                            )
                        }
                    }
                }
            }
            if (selectedGroup != null && selectedQuest != null) {
                QuestDetailPanel(
                    group = selectedGroup,
                    quest = selectedQuest,
                    claimingKey = state.claimingKey,
                    onClaim = onClaim,
                    onClose = { selectedQuestKey = null },
                )
            }
        }
    }
}

@Composable
private fun QuestDetailPanel(group: QuestGroup, quest: Quest, claimingKey: String?, onClaim: (String) -> Unit, onClose: () -> Unit) {
    Surface(color = AccessDefaults.Surface, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(Modifier.fillMaxWidth().heightIn(max = 280.dp).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Image(painterResource(badgeFor(group, quest)), null, modifier = Modifier.size(56.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(quest.title, style = MaterialTheme.typography.titleMedium, color = AccessDefaults.TextPrimary)
                    Text(quest.description, style = MaterialTheme.typography.bodySmall, color = AccessDefaults.TextSecondary)
                }
                IconButton(onClick = onClose) {
                    Icon(painterResource(AccessIcons.Close), stringResource(Res.string.quests_close), tint = AccessDefaults.TextPrimary)
                }
            }
            when {
                quest.claimed -> Text(stringResource(Res.string.quests_claimed_label), style = MaterialTheme.typography.bodySmall,
                    color = AccessDefaults.Accent, modifier = Modifier.padding(top = 12.dp))
                quest.completed -> BaseButton(
                    text = stringResource(Res.string.quests_claim_format, quest.points),
                    onClick = { onClaim(quest.key) },
                    enabled = claimingKey == null,
                    isLoading = claimingKey == quest.key,
                    loadingText = stringResource(Res.string.quests_collecting),
                    filled = true,
                    modifier = Modifier.padding(top = 12.dp),
                )
                else -> {
                    BaseProgressBar(
                        progress = (quest.progress.toFloat() / quest.target.coerceAtLeast(1)).coerceIn(0f, 1f),
                        height = 5.dp, trackColor = AccessDefaults.SurfaceElevated,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    )
                    Text(stringResource(Res.string.quests_level_progress, quest.progress, quest.target, quest.points),
                        style = MaterialTheme.typography.bodySmall, color = AccessDefaults.TextSecondary)
                }
            }
        }
    }
}

@Composable
internal fun groupTitle(group: QuestGroup): String = when (group.family) {
    QuestFamily.Meet -> stringResource(Res.string.quests_family_meet)
    QuestFamily.Streak -> stringResource(Res.string.quests_family_streak)
    QuestFamily.Beat -> stringResource(Res.string.quests_family_beat)
    QuestFamily.Rematch -> stringResource(Res.string.quests_family_rematch)
    QuestFamily.Revenge -> stringResource(Res.string.quests_family_revenge)
    QuestFamily.Other -> group.current.title
}

internal fun badgeFor(group: QuestGroup, quest: Quest = group.current): DrawableResource = when (group.family) {
    QuestFamily.Meet -> when {
        quest.target >= 15 -> Res.drawable.quest_badge_meet_15
        quest.target >= 10 -> Res.drawable.quest_badge_meet_10
        else -> Res.drawable.quest_badge_meet_5
    }
    QuestFamily.Streak -> if (quest.target >= 5) Res.drawable.quest_badge_win_streak_5 else Res.drawable.quest_badge_win_streak_3
    QuestFamily.Beat, QuestFamily.Revenge -> Res.drawable.quest_badge_rematch_win
    QuestFamily.Rematch -> Res.drawable.quest_badge_first_opponent
    QuestFamily.Other -> Res.drawable.quest_badge_points_earned
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun QuestsCollectionPreview() {
    CrewTheme {
        QuestsScreen(
            state = QuestsState(isLoading = false, quests = previewQuests()),
            onBack = {}, onClaim = {},
        )
    }
}

private fun previewQuests() = listOf(
    Quest("meet-5", "Meet 5 people", "Play with 5 different people.", 10, 5, 5, true, true),
    Quest("meet-10", "Meet 10 people", "Play with 10 different people.", 20, 10, 7, false, false),
    Quest("meet-15", "Meet 15 people", "Play with 15 different people.", 30, 15, 7, false, false),
    Quest("win-streak-3", "Win 3 in a row", "Win 3 matches without losing.", 15, 3, 3, true, false),
    Quest("win-streak-5", "Win 5 in a row", "Win 5 matches without losing.", 25, 5, 3, false, false),
    Quest("beat-3", "Beat 3 people", "Win against 3 different opponents.", 15, 3, 2, false, false),
    Quest("rematch-master", "Rematch master", "Play twice against 3 opponents.", 20, 3, 1, false, false),
    Quest("revenge-win", "Revenge win", "Beat someone who beat you earlier.", 15, 1, 1, true, true),
)
