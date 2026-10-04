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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.component.layout.FullScreenLoader
import com.kaanf.core.designsystem.modifier.verticalGradientScrim
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.game.domain.model.LeaderboardEntry
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.leaderboard_column_player
import crew.feature.game.presentation.generated.resources.leaderboard_column_points
import crew.feature.game.presentation.generated.resources.leaderboard_column_rank
import crew.feature.game.presentation.generated.resources.leaderboard_podium_points_format
import crew.feature.game.presentation.generated.resources.leaderboard_you_label
import org.jetbrains.compose.resources.stringResource
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.emitter.Emitter
import io.github.vinceglb.confettikit.core.models.Shape
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.milliseconds
import org.koin.compose.viewmodel.koinViewModel

/**
 * MatchContainerScreen içindeki sıralama tab'ı. Yalnız etkinlik bittiğinde açılır
 * (oyun sürerken tab kilitli), o yüzden tek varyant: podyum + altında kayan sıralama sheet'i.
 * Scaffold/top bar/bottom bar container'a ait; burası yalnız içerik.
 * VM, Game route entry'sine scope'lanır (eventId oradaki SavedStateHandle'dan).
 */
@Composable
fun LeaderboardTab(
    modifier: Modifier = Modifier,
    onCelebrationDone: () -> Unit = {},
) {
    val viewModel: LeaderboardViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Konfeti bitti ya da hiç oynamayacak (podyumsuz oda): container sıradaki adıma geçebilir.
    val isCelebrationDone = !state.isLoading && (!state.isConfettiPending || state.entries.size < 2)
    LaunchedEffect(isCelebrationDone) {
        if (isCelebrationDone) onCelebrationDone()
    }

    LeaderboardContent(
        state = state,
        onConfettiFinished = viewModel::onConfettiFinished,
        modifier = modifier,
    )
}

@Composable
fun LeaderboardContent(
    state: LeaderboardState,
    modifier: Modifier = Modifier,
    onConfettiFinished: () -> Unit = {},
) {
    if (state.isLoading) {
        FullScreenLoader(modifier = modifier)
        return
    }
    // Tek oyunculu odada podyum anlamsız; herkes listede kalır.
    val podiumCount = if (state.entries.size >= 2) minOf(3, state.entries.size) else 0
    Box(modifier = modifier.fillMaxSize()) {
        // Ekran sabit: yalnız sheet'in içindeki liste kayar.
        Column(modifier = Modifier.fillMaxSize()) {
            if (podiumCount > 0) {
                Podium(
                    topEntries = state.entries.take(podiumCount),
                    currentUserId = state.currentUserId,
                )
            }
            LeaderboardSheet(
                entries = state.entries.drop(podiumCount),
                currentUserId = state.currentUserId,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 24.dp),
            )
        }
        if (state.isConfettiPending && podiumCount > 0) {
            ConfettiBurst(
                onFinished = onConfettiFinished,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/**
 * Podyumun iki yanından içeri doğru tek seferlik konfeti patlaması.
 * Kütüphanenin kare döngüsü parçacıklar bitse de durmuyor (her kare yeniden çizim);
 * o yüzden ömür dolunca composable'ı ağaçtan çıkarıyoruz.
 */
@Composable
private fun ConfettiBurst(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val parties = remember {
        val colors = ConfettiColors.map { it.toArgb() }
        listOf(0.0 to ConfettiAngleLeft, 1.0 to ConfettiAngleRight).map { (x, angle) ->
            Party(
                angle = angle,
                spread = 50,
                speed = 25f,
                maxSpeed = 55f,
                damping = 0.9f,
                colors = colors,
                shapes = listOf(Shape.Square, Shape.Rectangle(0.4f)),
                timeToLive = ConfettiTimeToLiveMillis,
                position = Position.Relative(x, 0.3),
                emitter = Emitter(duration = 150.milliseconds).max(70),
            )
        }
    }
    val currentOnFinished by rememberUpdatedState(onFinished)
    LaunchedEffect(Unit) {
        delay(ConfettiLifetime)
        currentOnFinished()
    }
    ConfettiKit(modifier = modifier, parties = parties)
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
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, top = 48.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        ordered.forEach { entry ->
            PodiumPlace(
                entry = entry,
                isCurrentUser = entry.userId == currentUserId,
                modifier = Modifier.weight(if (entry.rank == 1) 1.15f else 1f),
            )
        }
    }
}

@Composable
private fun PodiumPlace(
    entry: LeaderboardEntry,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
) {
    val isFirst = entry.rank == 1
    val medal = medalColorFor(entry.rank)
    Column(
        // Birinci, ikinci ve üçüncünün üstüne çıkar.
        modifier = modifier.padding(bottom = if (isFirst) 34.dp else 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            AvatarCircle(
                content = avatarContentFor(
                    imageUrl = entry.profilePictureUrl,
                    initialsLabel = entry.fullName.take(1).uppercase(),
                    seed = entry.userId,
                ),
                avatarSize = if (isFirst) 92 else 76,
                modifier = Modifier
                    .border(width = 2.dp, color = medal, shape = CircleShape)
                    .padding(3.dp),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 13.dp)
                    .size(32.dp)
                    .background(color = AccessDefaults.Background, shape = CircleShape)
                    .padding(3.dp)
                    .background(color = medal, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${entry.rank}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = AccessDefaults.OnAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    ),
                )
            }
        }
        Text(
            text = entry.fullName,
            style = MaterialTheme.typography.headlineSmall.copy(
                color = if (isCurrentUser) AccessDefaults.Accent else AccessDefaults.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 22.dp),
        )
        Text(
            text = stringResource(Res.string.leaderboard_podium_points_format, entry.score),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                fontFeatureSettings = "tnum",
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
        )
    }
}

@Composable
private fun LeaderboardSheet(
    entries: List<LeaderboardEntry>,
    currentUserId: String?,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    // Kullanıcının satırını sheet'in görünür alanında ortala. Liste uçlarında LazyColumn
    // sınırlıyor; ilk/son satırlar tam ortaya gelemez, kenarda kalır.
    LaunchedEffect(entries, currentUserId) {
        val index = entries.indexOfFirst { it.userId == currentUserId }
        if (index < 0) return@LaunchedEffect
        val layoutInfo = snapshotFlow { listState.layoutInfo }.first { it.viewportSize.height > 0 }
        // Alt tab bar contentPadding bandının üstüne biniyor; orası "görünür" sayılmaz.
        val visibleHeight = layoutInfo.viewportSize.height - layoutInfo.afterContentPadding
        val rowHeight = with(density) { RowHeight.roundToPx() }
        listState.scrollToItem(index, scrollOffset = -(visibleHeight - rowHeight) / 2)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(AccessDefaults.Surface),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally)
                .size(width = 40.dp, height = 4.dp)
                .background(
                    color = AccessDefaults.TextPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(2.dp),
                ),
        )
        SheetColumnsHeader()
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 84.dp),
            ) {
                items(entries, key = { it.userId }) { entry ->
                    LeaderboardRow(
                        entry = entry,
                        isCurrentUser = entry.userId == currentUserId,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(110.dp)
                    .verticalGradientScrim(SheetFade),
            )
        }
    }
}

@Composable
private fun SheetColumnsHeader() {
    val style = MaterialTheme.typography.bodySmall.copy(color = AccessDefaults.TextMuted)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 26.dp, end = 26.dp, top = 16.dp, bottom = 4.dp),
    ) {
        Text(
            text = stringResource(Res.string.leaderboard_column_rank),
            style = style,
            // Oyuncu başlığı isim sütununa hizalı: sıra + avatar + iki boşluk.
            modifier = Modifier.width(RankWidth + AvatarSize.dp + RowSpacing * 2),
        )
        Text(
            text = stringResource(Res.string.leaderboard_column_player),
            style = style,
            modifier = Modifier.weight(1f),
        )
        Text(text = stringResource(Res.string.leaderboard_column_points), style = style)
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
            .height(RowHeight)
            .then(if (isCurrentUser) Modifier.currentUserHighlight() else Modifier)
            .padding(horizontal = 26.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(RowSpacing),
    ) {
        Text(
            text = "${entry.rank}",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = AccessDefaults.TextSecondary,
                fontSize = 16.sp,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(RankWidth),
        )
        AvatarCircle(
            content = avatarContentFor(
                imageUrl = entry.profilePictureUrl,
                initialsLabel = entry.fullName.take(1).uppercase(),
                seed = entry.userId,
            ),
            avatarSize = AvatarSize,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.fullName,
                style = MaterialTheme.typography.titleMedium.copy(color = AccessDefaults.TextPrimary),
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
            style = MaterialTheme.typography.headlineSmall.copy(
                color = AccessDefaults.Accent,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                fontFeatureSettings = "tnum",
            ),
        )
    }
}

/** Soldan sağa sönen accent zemin + sol kenarda accent çubuk. */
private fun Modifier.currentUserHighlight(): Modifier = this
    .background(CurrentUserBrush)
    .drawBehind {
        drawRect(color = AccessDefaults.Accent, size = Size(4.dp.toPx(), size.height))
    }

private fun medalColorFor(rank: Int): Color = when (rank) {
    1 -> MedalGold
    2 -> MedalSilver
    else -> MedalBronze
}

private val RowHeight = 68.dp
private val RankWidth = 30.dp
private val RowSpacing = 12.dp
private const val AvatarSize = 44

private val MedalGold = Color(0xFFFFC94A)
private val MedalSilver = Color(0xFFD5DCE3)
private val MedalBronze = Color(0xFFE89A6A)
private val ConfettiColors = listOf(AccessDefaults.Accent, AccessDefaults.TextPrimary, MedalGold)
// Açılar saat yönünde, 0° sağ: sol top yukarı-sağa, sağ top yukarı-sola ateşler.
private const val ConfettiAngleLeft = 300
private const val ConfettiAngleRight = 240
private const val ConfettiTimeToLiveMillis = 3_000L
// Emisyon + ömür + kütüphanenin 850ms'lik fade-out'u.
private val ConfettiLifetime = 4_200.milliseconds

private val CurrentUserBrush = Brush.horizontalGradient(
    0f to AccessDefaults.Accent.copy(alpha = 0.18f),
    0.75f to AccessDefaults.Accent.copy(alpha = 0.05f),
    1f to AccessDefaults.Accent.copy(alpha = 0f),
)
private val SheetFade = Brush.verticalGradient(
    0f to AccessDefaults.Surface.copy(alpha = 0f),
    0.8f to AccessDefaults.Surface,
)
