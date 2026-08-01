package com.kaanf.game.presentation.memories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.presentation.util.mediapicker.rememberCameraLauncher
import com.kaanf.game.domain.model.EventMemory
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.memories_roll_shots_label
import crew.feature.game.presentation.generated.resources.memories_roll_subtitle
import crew.feature.game.presentation.generated.resources.memories_roll_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TonightsRollSection(modifier: Modifier = Modifier) {
    val viewModel: MemoriesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showSheet by rememberSaveable { mutableStateOf(false) }
    var lightboxMemory by remember { mutableStateOf<EventMemory?>(null) }

    val cameraLauncher = rememberCameraLauncher { picked ->
        viewModel.upload(picked.bytes, picked.mimeType)
    }

    TonightsRollCard(
        shotCount = state.memories.size,
        thumbnailUrls = state.memories.take(3).map { it.imageUrl },
        onClick = { showSheet = true },
        modifier = modifier,
    )

    if (showSheet) {
        ContainerBottomSheet(onDismiss = { showSheet = false }) {
            LaunchedEffect(Unit) { viewModel.refresh() }
            MemoriesSheet(
                state = state,
                onSnapClick = { cameraLauncher.launch() },
                onDeleteMemory = viewModel::delete,
                onMemoryClick = { lightboxMemory = it },
            )
        }
    }

    lightboxMemory?.let { memory ->
        MemoryLightbox(memory = memory, onDismiss = { lightboxMemory = null })
    }
}

@Composable
private fun TonightsRollCard(
    shotCount: Int,
    thumbnailUrls: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MemoriesEntryCard(
        icon = AccessIcons.Camera,
        title = stringResource(Res.string.memories_roll_title),
        onClick = onClick,
        modifier = modifier,
        subtitle = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(
                    painter = painterResource(AccessIcons.Lock),
                    contentDescription = null,
                    tint = AccessDefaults.Coral.copy(alpha = 0.7f),
                    modifier = Modifier.size(10.dp),
                )
                Text(
                    text = stringResource(Res.string.memories_roll_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextMuted,
                        fontSize = 11.sp,
                    ),
                )
            }
        },
        trailing = {
            if (thumbnailUrls.isNotEmpty()) {
                // Üst üste binen stack: negatif spacing, offset gibi ölü genişlik bırakmaz.
                Row(horizontalArrangement = Arrangement.spacedBy((-11).dp)) {
                    thumbnailUrls.forEach { url ->
                        BaseImage(
                            imageUrl = url,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(27.dp)
                                .clip(AccessShapes.Small)
                                .border(
                                    width = 2.dp,
                                    color = AccessDefaults.Surface,
                                    shape = AccessShapes.Small,
                                ),
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(11.dp))
                    .background(AccessDefaults.Coral.copy(alpha = 0.1f))
                    .border(
                        width = 1.dp,
                        color = AccessDefaults.Coral.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(11.dp),
                    )
                    .padding(horizontal = 9.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "$shotCount",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.Coral,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = stringResource(Res.string.memories_roll_shots_label).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.Coral.copy(alpha = 0.7f),
                        fontSize = 8.sp,
                        letterSpacing = 1.sp,
                    ),
                )
            }
        },
    )
}
