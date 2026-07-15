package com.kaanf.game.presentation.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.game.domain.model.LeaderboardEntry
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.leaderboard_eyebrow
import crew.feature.game.presentation.generated.resources.leaderboard_full_board_label
import crew.feature.game.presentation.generated.resources.leaderboard_go_home_action
import crew.feature.game.presentation.generated.resources.leaderboard_players_count_format
import crew.feature.game.presentation.generated.resources.leaderboard_points_format
import crew.feature.game.presentation.generated.resources.leaderboard_title_highlight
import crew.feature.game.presentation.generated.resources.leaderboard_title_prefix
import crew.feature.game.presentation.generated.resources.leaderboard_top_bar_title
import crew.feature.game.presentation.generated.resources.leaderboard_you_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun LeaderboardRoot(
    viewModel: LeaderboardViewModel,
    onNavigateToDashboard: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LeaderboardScreen(
        state = state,
        onGoHomeClick = onNavigateToDashboard,
    )
}

@Composable
fun LeaderboardScreen(
    state: LeaderboardState,
    onGoHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val showPodium = state.entries.size >= 3

    // Kullanıcının satırını viewport'ta ortala. İki aşama: önce satırı görünür yap ki
    // viewport ve satır yüksekliği layout'tan okunabilsin, sonra ortalayan offset'le kaydır.
    LaunchedEffect(state.entries, state.currentUserId) {
        val index = state.entries.indexOfFirst { it.userId == state.currentUserId }
        if (index < 0) return@LaunchedEffect
        val headerCount = if (state.entries.size >= 3) 3 else 2
        val itemIndex = index + headerCount
        listState.scrollToItem(itemIndex)
        val layoutInfo = listState.layoutInfo
        val viewport = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
        val itemSize = layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == itemIndex }?.size ?: return@LaunchedEffect
        listState.scrollToItem(itemIndex, scrollOffset = -(viewport - itemSize) / 2)
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.GameLobby(stringResource(Res.string.leaderboard_top_bar_title)),
                elevated = { listState.canScrollBackward },
                onBackClick = onGoHomeClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item(key = "header") { WrapHeader() }
                    if (showPodium) {
                        item(key = "podium") {
                            Podium(
                                topThree = state.entries.take(3),
                                currentUserId = state.currentUserId,
                            )
                        }
                    }
                    item(key = "board_header") { FullBoardHeader(playerCount = state.entries.size) }
                    items(state.entries, key = { it.userId }) { entry ->
                        LeaderboardRow(
                            entry = entry,
                            isCurrentUser = entry.userId == state.currentUserId,
                        )
                    }
                }
            }

            BaseButton(
                text = stringResource(Res.string.leaderboard_go_home_action),
                onClick = onGoHomeClick,
                filled = true,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun WrapHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(Res.string.leaderboard_eyebrow),
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.Accent,
                fontSize = 12.sp,
            ),
        )
        Text(
            text = buildAnnotatedString {
                append(stringResource(Res.string.leaderboard_title_prefix))
                withStyle(
                    style = SpanStyle(
                        color = AccessDefaults.Accent,
                        shadow = Shadow(color = AccessDefaults.AccentGlow, blurRadius = 24f),
                    ),
                ) {
                    append(stringResource(Res.string.leaderboard_title_highlight))
                }
            },
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Podium(
    topThree: List<LeaderboardEntry>,
    currentUserId: String?,
    modifier: Modifier = Modifier,
) {
    // Tasarımdaki sıralama: 2. — 1. — 3.
    val ordered = listOf(topThree[1], topThree[0], topThree[2])
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        ordered.forEach { entry ->
            PodiumColumn(
                entry = entry,
                isCurrentUser = entry.userId == currentUserId,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PodiumColumn(
    entry: LeaderboardEntry,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
) {
    val isFirst = entry.rank == 1
    val blockHeight = when (entry.rank) {
        1 -> 92.dp
        2 -> 70.dp
        else -> 54.dp
    }
    val blockShape = RoundedCornerShape(
        topStart = 10.dp,
        topEnd = 10.dp,
        bottomStart = 4.dp,
        bottomEnd = 4.dp,
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AvatarCircle(
            content = avatarContentFor(
                imageUrl = entry.profilePictureUrl,
                initialsLabel = entry.fullName.take(1).uppercase(),
                seed = entry.userId,
            ),
            avatarSize = if (isFirst) 64 else 46,
            borderColor = if (isFirst) AccessDefaults.Accent else AccessDefaults.AvatarBorder,
        )
        Text(
            text = entry.fullName,
            style = MaterialTheme.typography.titleSmall.copy(
                color = if (isCurrentUser) AccessDefaults.Accent else AccessDefaults.TextPrimary,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(Res.string.leaderboard_points_format, entry.score),
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 11.sp,
            ),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(blockHeight)
                .then(
                    if (isFirst) {
                        Modifier.background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    lerp(AccessDefaults.Surface, AccessDefaults.Accent, 0.35f),
                                    AccessDefaults.Surface,
                                ),
                            ),
                            shape = blockShape,
                        )
                    } else {
                        Modifier.background(
                            color = AccessDefaults.SurfaceElevated,
                            shape = blockShape,
                        )
                    },
                )
                .border(
                    width = 1.dp,
                    color = if (isFirst) {
                        AccessDefaults.Accent.copy(alpha = 0.35f)
                    } else {
                        AccessDefaults.BorderSoft
                    },
                    shape = blockShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${entry.rank}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = if (isFirst) AccessDefaults.Accent else AccessDefaults.TextSecondary,
                ),
            )
        }
    }
}

@Composable
private fun FullBoardHeader(
    playerCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.leaderboard_full_board_label),
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp,
            ),
        )
        Text(
            text = stringResource(Res.string.leaderboard_players_count_format, playerCount),
            style = MaterialTheme.typography.labelSmall.copy(color = AccessDefaults.TextFaint),
        )
    }
}

@Composable
private fun LeaderboardRow(
    entry: LeaderboardEntry,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isCurrentUser) AccessDefaults.SurfaceElevated else AccessDefaults.Surface,
                shape = AccessShapes.Medium,
            )
            .border(
                width = 1.dp,
                color = if (isCurrentUser) AccessDefaults.AccentFocusRing else AccessDefaults.BorderSoft,
                shape = AccessShapes.Medium,
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "${entry.rank}",
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isCurrentUser) AccessDefaults.Accent else AccessDefaults.TextMuted,
                fontSize = 12.sp,
            ),
            modifier = Modifier.width(24.dp),
        )
        AvatarCircle(
            content = avatarContentFor(
                imageUrl = entry.profilePictureUrl,
                initialsLabel = entry.fullName.take(1).uppercase(),
                seed = entry.userId,
            ),
            avatarSize = 40,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.fullName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isCurrentUser) FontWeight.SemiBold else FontWeight.Normal,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (isCurrentUser) {
                Text(
                    text = stringResource(Res.string.leaderboard_you_label),
                    style = MaterialTheme.typography.labelSmall.copy(color = AccessDefaults.Accent),
                )
            }
        }
        Text(
            text = "${entry.score}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
    }
}
