package com.kaanf.game.presentation.memories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.modifier.surfaceCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.game.domain.model.EventMemory
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.memories_reveal_card_subtitle_format
import crew.feature.game.presentation.generated.resources.memories_reveal_card_title
import crew.feature.game.presentation.generated.resources.memories_reveal_eyebrow
import crew.feature.game.presentation.generated.resources.memories_reveal_subtitle_format
import crew.feature.game.presentation.generated.resources.memories_reveal_title
import crew.feature.game.presentation.generated.resources.memories_you_badge
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Leaderboard'daki "The roll developed" girişi. Etkinlik bittiği için sunucu artık tüm
 * odanın fotoğraflarını döner; karta dokununca rulo sheet olarak açılır, kareye dokununca
 * tam ekran görüntülenir. Hiç fotoğraf yoksa kart gösterilmez.
 */
@Composable
fun MemoriesRevealEntry(modifier: Modifier = Modifier) {
    val viewModel: MemoriesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showSheet by rememberSaveable { mutableStateOf(false) }
    var lightboxMemory by remember { mutableStateOf<EventMemory?>(null) }

    // VM oyun sırasında yalnız kendi fotoğraflarımı tutmuş olabilir; etkinlik bitince
    // aynı endpoint tüm ruloyu döndüğünden burada tazelemek şart.
    LaunchedEffect(Unit) { viewModel.refresh() }

    if (state.memories.isEmpty()) return

    MemoriesRevealCard(
        count = state.memories.size,
        onClick = { showSheet = true },
        modifier = modifier,
    )

    if (showSheet) {
        ContainerBottomSheet(onDismiss = { showSheet = false }) {
            // İmzalı URL'ler kısa ömürlü; taze listeyi sheet açıldıktan sonra çek ki
            // ağ isteği açılış animasyonunun ilk karesiyle yarışmasın.
            LaunchedEffect(Unit) { viewModel.refresh() }
            MemoriesRevealSheet(
                memories = state.memories,
                endReached = state.endReached,
                onLoadMore = viewModel::loadNextPage,
                onMemoryClick = { lightboxMemory = it },
            )
        }
    }

    lightboxMemory?.let { memory ->
        MemoryLightbox(memory = memory, onDismiss = { lightboxMemory = null })
    }
}

@Composable
private fun MemoriesRevealCard(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(cardShape)
            .background(AccessDefaults.Surface)
            .border(
                width = 1.dp,
                color = AccessDefaults.Coral.copy(alpha = 0.34f),
                shape = cardShape,
            )
            .clickable(onClick = onClick)
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AccessDefaults.Coral.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(AccessIcons.LockOpen),
                contentDescription = null,
                tint = AccessDefaults.Coral,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = stringResource(Res.string.memories_reveal_card_title),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = stringResource(Res.string.memories_reveal_card_subtitle_format, count),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 11.sp,
                ),
            )
        }
        Icon(
            painter = painterResource(AccessIcons.RightChevron),
            contentDescription = null,
            tint = AccessDefaults.Coral,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun MemoriesRevealSheet(
    memories: List<EventMemory>,
    endReached: Boolean,
    onLoadMore: () -> Unit,
    onMemoryClick: (EventMemory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                painter = painterResource(AccessIcons.LockOpen),
                contentDescription = null,
                tint = AccessDefaults.Coral,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = stringResource(Res.string.memories_reveal_eyebrow).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.Coral,
                    letterSpacing = 1.5.sp,
                ),
            )
        }
        Text(
            text = stringResource(Res.string.memories_reveal_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(Res.string.memories_reveal_subtitle_format, memories.size),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextMuted,
                lineHeight = 18.sp,
            ),
            modifier = Modifier.padding(top = 4.dp, bottom = 15.dp),
        )

        val gridState = rememberLazyGridState()
        // Sonraki sayfayı yalnız kullanıcı sona yaklaşınca çek; loader item'ın ilk
        // kompozisyonunda tetiklersek sheet daha açılırken ikinci sayfa isteği gidiyordu.
        LaunchedEffect(gridState) {
            snapshotFlow {
                val info = gridState.layoutInfo
                val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
                lastVisible >= info.totalItemsCount - 4
            }.collect { nearEnd -> if (nearEnd) onLoadMore() }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            verticalArrangement = Arrangement.spacedBy(9.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            modifier = Modifier.heightIn(max = 420.dp),
        ) {
            items(memories, key = { it.id }) { memory ->
                RevealCell(
                    memory = memory,
                    onClick = { onMemoryClick(memory) },
                )
            }
            if (!endReached) {
                item(key = "page_loader", span = { GridItemSpan(maxLineSpan) }) {
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
private fun RevealCell(
    memory: EventMemory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cellShape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .aspectRatio(1f / 1.15f)
            .clip(cellShape)
            .surfaceCard(shape = cellShape, backgroundColor = AccessDefaults.SurfaceElevated)
            .clickable(onClick = onClick),
    ) {
        BaseImage(
            imageUrl = memory.imageUrl,
            contentScale = ContentScale.Crop,
            cacheKey = memory.id,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.78f),
                        ),
                    ),
                ),
        )

        if (memory.isMine) {
            Text(
                text = stringResource(Res.string.memories_you_badge),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.OnAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(AccessDefaults.Accent, RoundedCornerShape(999.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp),
            )
        }

        Text(
            text = "${memory.ownerName} · ${memory.capturedAt.toClockLabel()}",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 11.dp, vertical = 11.dp),
        )
    }
}
