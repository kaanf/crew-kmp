package com.kaanf.game.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.modifier.surfaceCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.game.domain.model.MatchHistoryEntry
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.history_empty_description
import crew.feature.game.presentation.generated.resources.history_empty_title
import crew.feature.game.presentation.generated.resources.history_kind_lost
import crew.feature.game.presentation.generated.resources.history_kind_opponent_left
import crew.feature.game.presentation.generated.resources.history_kind_won
import crew.feature.game.presentation.generated.resources.history_kind_you_left
import crew.feature.game.presentation.generated.resources.history_match_log_label
import com.kaanf.game.presentation.util.toClockText
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * MatchContainerScreen içindeki "Your night" tab'ı: biten maçların akışı (match log).
 * VM, Game route entry'sine scope'lanır (eventId oradaki SavedStateHandle'dan).
 */
@Composable
fun HistoryTab(modifier: Modifier = Modifier) {
    val viewModel: HistoryViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }

        state.entries.isEmpty() -> HistoryEmptyState(modifier = modifier)

        else -> LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 84.dp),
        ) {
            item(key = "log_header") {
                Text(
                    text = stringResource(Res.string.history_match_log_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextMuted,
                        fontSize = 12.sp,
                    ),
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                )
            }
            items(state.entries, key = { it.matchId }) { entry ->
                MatchLogCard(entry = entry)
            }
            if (!state.endReached) {
                item(key = "page_loader") {
                    // Satır görünür olunca sıradaki sayfayı çek (sonsuz kaydırma).
                    LaunchedEffect(state.entries.size) { viewModel.loadNextPage() }
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchLogCard(
    entry: MatchHistoryEntry,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .surfaceCard()
            .padding(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AvatarCircle(
                content = avatarContentFor(
                    imageUrl = entry.opponentAvatarUrl,
                    initialsLabel = entry.opponentFullName.take(1).uppercase(),
                    seed = entry.opponentUserId ?: entry.matchId,
                ),
                avatarSize = 40,
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = entry.opponentFullName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AccessDefaults.TextPrimary,
                            fontWeight = FontWeight.Medium,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Text(
                        text = entry.occurredAt.toClockText(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccessDefaults.TextMuted,
                            fontSize = 11.sp,
                        ),
                    )
                }
                Text(
                    text = stringResource(entry.kindLabelRes()),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextMuted,
                    ),
                )
            }
            Text(
                text = if (entry.myPoints > 0) "+${entry.myPoints}" else "${entry.myPoints}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = when {
                        entry.myPoints < 0 -> AccessDefaults.Rose
                        entry.myPoints == 0 -> AccessDefaults.TextMuted
                        else -> AccessDefaults.Accent
                    },
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
        entry.taskTitle?.let { title ->
            Text(
                text = "“$title”",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontStyle = FontStyle.Italic,
                ),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

private fun MatchHistoryEntry.kindLabelRes() = when {
    cancelled && won -> Res.string.history_kind_opponent_left
    cancelled -> Res.string.history_kind_you_left
    won -> Res.string.history_kind_won
    else -> Res.string.history_kind_lost
}

@Composable
private fun HistoryEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(AccessIcons.Clock),
                contentDescription = null,
                tint = AccessDefaults.TextFaint,
                modifier = Modifier.size(36.dp),
            )
            Text(
                text = stringResource(Res.string.history_empty_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AccessDefaults.TextPrimary,
                ),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.history_empty_description),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AccessDefaults.TextMuted,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}
