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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.game.domain.model.EventMemory
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.memories_delete_action
import crew.feature.game.presentation.generated.resources.memories_delete_confirm_title
import crew.feature.game.presentation.generated.resources.memories_delete_keep
import crew.feature.game.presentation.generated.resources.memories_limit_reached
import crew.feature.game.presentation.generated.resources.memories_sheet_description
import crew.feature.game.presentation.generated.resources.memories_sheet_seal_label
import crew.feature.game.presentation.generated.resources.memories_sheet_title
import crew.feature.game.presentation.generated.resources.memories_snap_action
import crew.feature.game.presentation.generated.resources.memories_snap_uploading
import crew.feature.game.presentation.generated.resources.memories_your_shots_label
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Sunucudaki MAX_MEMORIES_PER_PARTICIPANT ile aynı; asıl kural sunucuda. */
private const val MAX_SHOTS_PER_PLAYER = 3

/**
 * Oyun ekranından açılan "Tonight's roll" sheet içeriği. Kullanıcı yalnız kendi
 * çektiklerini görür; kameradan yeni kare ekler, kendi karesini siler, dokununca
 * tam ekran açılır. Odanın rulosu etkinlik bitene dek mühürlüdür.
 */
@Composable
fun MemoriesSheet(
    state: MemoriesState,
    onSnapClick: () -> Unit,
    onDeleteMemory: (String) -> Unit,
    onMemoryClick: (EventMemory) -> Unit,
    modifier: Modifier = Modifier,
) {
    var confirmDeleteId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
    ) {
        Text(
            text = stringResource(Res.string.memories_sheet_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(Res.string.memories_sheet_description),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextMuted,
                lineHeight = 18.sp,
            ),
            modifier = Modifier.padding(top = 6.dp),
        )

        val limitReached = state.memories.count { it.isMine } >= MAX_SHOTS_PER_PLAYER
        BaseButton(
            text = if (limitReached) {
                stringResource(Res.string.memories_limit_reached)
            } else {
                stringResource(Res.string.memories_snap_action)
            },
            onClick = onSnapClick,
            enabled = !limitReached,
            isLoading = state.isUploading,
            loadingText = stringResource(Res.string.memories_snap_uploading),
            filled = true,
            leadingIcon = if (limitReached) null else AccessIcons.Camera,
            backgroundColor = AccessDefaults.Coral,
            // borderColor arka planla aynı: filled default'u accent border çiziyor, istenmiyor.
            borderColor = AccessDefaults.Coral,
            contentColor = AccessDefaults.Background,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        )

        if (state.memories.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 18.dp, bottom = 10.dp),
            ) {
                Text(
                    text = stringResource(Res.string.memories_your_shots_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextSecondary,
                        letterSpacing = 1.sp,
                    ),
                )
                Text(
                    text = "${state.memories.size}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.Coral,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier
                        .background(
                            color = AccessDefaults.Coral.copy(alpha = 0.13f),
                            shape = RoundedCornerShape(999.dp),
                        )
                        .padding(horizontal = 8.dp, vertical = 1.dp),
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.heightIn(max = 340.dp),
            ) {
                items(state.memories, key = { it.id }) { memory ->
                    MyShotCell(
                        memory = memory,
                        showDeleteConfirm = confirmDeleteId == memory.id,
                        onClick = { onMemoryClick(memory) },
                        onDeleteRequest = { confirmDeleteId = memory.id },
                        onDeleteConfirm = {
                            confirmDeleteId = null
                            onDeleteMemory(memory.id)
                        },
                        onDeleteCancel = { confirmDeleteId = null },
                    )
                }
            }
        }
    }
}

@Composable
private fun MyShotCell(
    memory: EventMemory,
    showDeleteConfirm: Boolean,
    onClick: () -> Unit,
    onDeleteRequest: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cellShape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .aspectRatio(1f / 1.1f)
            .clip(cellShape)
            .background(AccessDefaults.SurfaceElevated)
            .border(width = 1.dp, color = AccessDefaults.BorderSoft, shape = cellShape)
            .clickable(enabled = !showDeleteConfirm, onClick = onClick),
    ) {
        BaseImage(
            imageUrl = memory.imageUrl,
            contentScale = ContentScale.Crop,
            cacheKey = memory.id,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                    ),
                )
                .padding(start = 10.dp, end = 10.dp, top = 18.dp, bottom = 9.dp),
        ) {
            Text(
                text = memory.capturedAt.toClockLabel(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }

        if (showDeleteConfirm) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AccessDefaults.Background.copy(alpha = 0.82f))
                    .clickable(onClick = onDeleteCancel),
                verticalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.memories_delete_confirm_title),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(
                        text = stringResource(Res.string.memories_delete_keep),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccessDefaults.TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(AccessDefaults.SurfaceHigh)
                            .clickable(onClick = onDeleteCancel)
                            .padding(horizontal = 13.dp, vertical = 7.dp),
                    )
                    Text(
                        text = stringResource(Res.string.memories_delete_action),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(AccessDefaults.Rose)
                            .clickable(onClick = onDeleteConfirm)
                            .padding(horizontal = 13.dp, vertical = 7.dp),
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(7.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(onClick = onDeleteRequest),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(AccessIcons.Trash),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp),
                )
            }
        }
    }
}
