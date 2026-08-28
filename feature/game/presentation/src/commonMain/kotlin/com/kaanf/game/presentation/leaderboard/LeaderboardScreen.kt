package com.kaanf.game.presentation.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kaanf.core.designsystem.component.header.SectionHeader
import com.kaanf.core.designsystem.component.layout.FullScreenLoader
import com.kaanf.core.designsystem.modifier.surfaceCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.game.domain.model.LeaderboardEntry
import com.kaanf.game.presentation.memories.MemoriesRevealEntry
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.leaderboard_eyebrow
import crew.feature.game.presentation.generated.resources.leaderboard_full_board_label
import crew.feature.game.presentation.generated.resources.leaderboard_players_count_format
import crew.feature.game.presentation.generated.resources.leaderboard_points_format
import crew.feature.game.presentation.generated.resources.leaderboard_title_highlight
import crew.feature.game.presentation.generated.resources.leaderboard_title_prefix
import crew.feature.game.presentation.generated.resources.leaderboard_you_label
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * MatchContainerScreen içindeki sıralama tab'ı. Yalnız etkinlik bittiğinde açılır
 * (oyun sürerken tab kilitli), o yüzden tek varyant: kutlama başlığı + podyum.
 * Scaffold/top bar/bottom bar container'a ait; burası yalnız içerik.
 * VM, Game route entry'sine scope'lanır (eventId oradaki SavedStateHandle'dan).
 */
@Composable
fun LeaderboardTab(modifier: Modifier = Modifier) {
    val viewModel: LeaderboardViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LeaderboardContent(
        state = state,
        modifier = modifier,
    )
}

@Composable
fun LeaderboardContent(
    state: LeaderboardState,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val showPodium = state.entries.size >= 2

    // Kullanıcının satırını viewport'ta ortala. İki aşama: önce satırı görünür yap ki
    // viewport ve satır yüksekliği layout'tan okunabilsin, sonra ortalayan offset'le kaydır.
    LaunchedEffect(state.entries, state.currentUserId) {
        val index = state.entries.indexOfFirst { it.userId == state.currentUserId }
        if (index < 0) return@LaunchedEffect
        // header + (podium) + memories + board_header; memories item boş da olsa index sayar.
        val headerCount = if (showPodium) 4 else 3
        val itemIndex = index + headerCount
        listState.scrollToItem(itemIndex)
        val layoutInfo = listState.layoutInfo
        val viewport = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
        val itemSize = layoutInfo.visibleItemsInfo
            .firstOrNull { it.index == itemIndex }?.size ?: return@LaunchedEffect
        listState.scrollToItem(itemIndex, scrollOffset = -(viewport - itemSize) / 2)
    }

    if (state.isLoading) {
        FullScreenLoader(modifier = modifier)
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 84.dp),
        ) {
            item(key = "header", contentType = "header") { WrapHeader() }
            if (showPodium) {
                item(key = "podium", contentType = "podium") {
                    Podium(
                        topEntries = state.entries.take(3),
                        currentUserId = state.currentUserId,
                    )
                }
            }
            item(key = "memories", contentType = "memories") { MemoriesRevealEntry() }
            item(key = "board_header", contentType = "board_header") { FullBoardHeader(playerCount = state.entries.size) }
            items(state.entries, key = { it.userId }, contentType = { "row" }) { entry ->
                LeaderboardRow(
                    entry = entry,
                    isCurrentUser = entry.userId == state.currentUserId,
                )
            }
        }
    }
}

@Composable
private fun WrapHeader(modifier: Modifier = Modifier) {
    SectionHeader(
        modifier = modifier.padding(top = 12.dp, bottom = 8.dp),
        eyebrow = stringResource(Res.string.leaderboard_eyebrow),
        eyebrowColor = AccessDefaults.Accent,
        title = buildAnnotatedString {
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
        titleStyle = MaterialTheme.typography.displayMedium.copy(
            color = AccessDefaults.TextPrimary,
        ),
        verticalSpacing = 6.dp,
    )
}

@Composable
private fun Podium(
    topEntries: List<LeaderboardEntry>,
    currentUserId: String?,
    modifier: Modifier = Modifier,
) {
    // Tasarımdaki sıralama: 2. — 1. — 3. Üçüncü yoksa (2 oyunculu oda) 2. — 1. kalır.
    val ordered = if (topEntries.size >= 3) {
        listOf(topEntries[1], topEntries[0], topEntries[2])
    } else {
        listOf(topEntries[1], topEntries[0])
    }
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
            // Uzun isim sütunun tamamını kaplayınca metin kendi kutusunda sola yaslanıyordu.
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )
        Text(
            text = stringResource(Res.string.leaderboard_points_format, entry.score),
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 11.sp,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
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
    label: String = stringResource(Res.string.leaderboard_full_board_label),
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
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
            .surfaceCard(
                backgroundColor = if (isCurrentUser) {
                    AccessDefaults.SurfaceElevated
                } else {
                    AccessDefaults.Surface
                },
                borderColor = if (isCurrentUser) {
                    AccessDefaults.AccentFocusRing
                } else {
                    AccessDefaults.BorderSoft
                },
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
                    color = AccessDefaults.TextPrimary,
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
            style = MaterialTheme.typography.titleMedium.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}
