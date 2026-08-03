package com.kaanf.game.presentation.quests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.header.SectionHeader
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.FullScreenLoader
import com.kaanf.core.designsystem.component.progressbar.BaseProgressBar
import com.kaanf.core.designsystem.modifier.surfaceCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.mediapicker.PickedImageData
import com.kaanf.core.presentation.util.mediapicker.rememberCameraLauncher
import com.kaanf.game.domain.model.EventMemory
import com.kaanf.game.domain.model.Quest
import com.kaanf.game.domain.model.QuestPhotoTag
import com.kaanf.game.presentation.memories.MemoryLightbox
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.quests_claim_format
import crew.feature.game.presentation.generated.resources.quests_claimed_label
import crew.feature.game.presentation.generated.resources.quests_photo_shot_by_format
import crew.feature.game.presentation.generated.resources.quests_photo_shot_by_you
import crew.feature.game.presentation.generated.resources.quests_photo_tags_format
import crew.feature.game.presentation.generated.resources.quests_photo_take_action
import crew.feature.game.presentation.generated.resources.quests_photo_with_format
import crew.feature.game.presentation.generated.resources.quests_progress_format
import crew.feature.game.presentation.generated.resources.quests_reward_format
import crew.feature.game.presentation.generated.resources.quests_title_highlight
import crew.feature.game.presentation.generated.resources.quests_title_prefix
import crew.feature.game.presentation.generated.resources.quests_top_bar_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun QuestsRoot(
    onBack: () -> Unit,
    viewModel: QuestsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    QuestsScreen(
        state = state,
        onBack = onBack,
        onClaim = viewModel::claim,
        onLoadParticipants = viewModel::loadTaggableParticipants,
        onSendPhoto = viewModel::submitPhoto,
    )
}

@Composable
fun QuestsScreen(
    state: QuestsState,
    onBack: () -> Unit,
    onClaim: (String) -> Unit,
    onLoadParticipants: () -> Unit,
    onSendPhoto: (
        questKey: String,
        tags: List<QuestPhotoTag>,
        image: ImageBitmap,
        onSent: () -> Unit,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Foto akışı: satırdan kamera → çekilen kare etiketleme ekranını açar → gönderince liste.
    var pendingQuest by remember { mutableStateOf<Quest?>(null) }
    var pendingPhoto by remember { mutableStateOf<PickedImageData?>(null) }
    var lightboxMemory by remember { mutableStateOf<EventMemory?>(null) }

    val cameraLauncher = rememberCameraLauncher { picked -> pendingPhoto = picked }
    val closePhotoFlow = {
        pendingPhoto = null
        pendingQuest = null
    }

    val photoQuest = pendingQuest
    val photo = pendingPhoto
    if (photoQuest != null && photo != null) {
        QuestPhotoTagScreen(
            modifier = modifier,
            quest = photoQuest,
            imageBytes = photo.bytes,
            participants = state.taggableParticipants,
            isUploading = state.isUploading,
            onLoadParticipants = onLoadParticipants,
            onBack = closePhotoFlow,
            onSend = { tags, image ->
                onSendPhoto(photoQuest.key, tags, image) { closePhotoFlow() }
            },
        )
        return
    }

    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                state = AppTopBarState.GameLobby(
                    stringResource(Res.string.quests_top_bar_title),
                ),
                onBackClick = onBack,
            )
        },
    ) { innerPadding ->
        if (state.isLoading) {
            FullScreenLoader(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                item(key = "header") { QuestsHeader() }
                items(state.quests, key = { it.key }) { quest ->
                    QuestCard(
                        quest = quest,
                        photo = state.photos[quest.key],
                        isClaiming = state.claimingKey == quest.key,
                        onClaim = { onClaim(quest.key) },
                        onTakePhoto = {
                            pendingQuest = quest
                            cameraLauncher.launch()
                        },
                        onPhotoClick = { memory -> lightboxMemory = memory },
                    )
                }
            }
        }
    }


    lightboxMemory?.let { memory ->
        MemoryLightbox(memory = memory, onDismiss = { lightboxMemory = null })
    }
}

@Composable
private fun QuestsHeader(modifier: Modifier = Modifier) {
    SectionHeader(
        modifier = modifier.padding(top = 12.dp, bottom = 8.dp),
        title = buildAnnotatedString {
            append(stringResource(Res.string.quests_title_prefix))
            withStyle(
                style = SpanStyle(
                    color = AccessDefaults.Accent,
                    shadow = Shadow(color = AccessDefaults.AccentGlow, blurRadius = 24f),
                ),
            ) {
                append(stringResource(Res.string.quests_title_highlight))
            }
        },
        titleStyle = MaterialTheme.typography.displaySmall.copy(
            color = AccessDefaults.TextPrimary,
        ),
        verticalSpacing = 6.dp,
    )
}

@Composable
private fun QuestCard(
    quest: Quest,
    photo: EventMemory?,
    isClaiming: Boolean,
    onClaim: () -> Unit,
    onTakePhoto: () -> Unit,
    onPhotoClick: (EventMemory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(22.dp)
    val cardModifier = modifier
        .fillMaxWidth()
        .alpha(if (quest.claimed) 0.62f else 1f)
        .surfaceCard(shape = cardShape)

    if (quest.completed) {
        Row(
            modifier = cardModifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            if (photo != null) {
                QuestPhotoThumb(photo = photo, onClick = { onPhotoClick(photo) })
            } else {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(AccessDefaults.Accent.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(AccessIcons.Check),
                        contentDescription = null,
                        tint = AccessDefaults.Accent,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextPrimary,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = photo?.captionLabel() ?: quest.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = AccessDefaults.TextMuted),
                )
            }
            when {
                quest.claimed -> Text(
                    text = stringResource(Res.string.quests_claimed_label),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AccessDefaults.TextFaint,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

                isClaiming -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = AccessDefaults.Accent,
                    strokeWidth = 2.dp,
                )

                else -> Text(
                    text = stringResource(Res.string.quests_claim_format, quest.points),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AccessDefaults.OnAccent,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier
                        .clip(AccessShapes.Pill)
                        .background(AccessDefaults.Accent)
                        .clickable(onClick = onClaim)
                        .padding(horizontal = 15.dp, vertical = 9.dp),
                )
            }
        }
    } else {
        Column(modifier = cardModifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextPrimary,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(Res.string.quests_reward_format, quest.points),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.Accent,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier
                        .background(
                            color = AccessDefaults.Accent.copy(alpha = 0.12f),
                            shape = AccessShapes.Pill,
                        )
                        .padding(horizontal = 9.dp, vertical = 3.dp),
                )
            }
            Text(
                text = quest.description,
                style = MaterialTheme.typography.bodySmall.copy(color = AccessDefaults.TextMuted),
                modifier = Modifier.padding(top = 6.dp),
            )

            if (quest.isPhoto) {
                // Foto questinde ilerleme çubuğu yok: tek kare ya var ya yok.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 14.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.quests_photo_tags_format, quest.requiredTags),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = AccessDefaults.TextMuted,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        modifier = Modifier
                            .clip(AccessShapes.Pill)
                            .background(AccessDefaults.Accent)
                            .clickable(onClick = onTakePhoto)
                            .padding(horizontal = 15.dp, vertical = 9.dp),
                    ) {
                        Icon(
                            painter = painterResource(AccessIcons.Camera),
                            contentDescription = null,
                            tint = AccessDefaults.OnAccent,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(Res.string.quests_photo_take_action),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = AccessDefaults.OnAccent,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                }
            } else {
                BaseProgressBar(
                    progress = quest.progress / quest.target.toFloat(),
                    height = 7.dp,
                    trackColor = AccessDefaults.SurfaceElevated,
                    modifier = Modifier.padding(top = 12.dp, bottom = 9.dp),
                )
                Text(
                    text = stringResource(
                        Res.string.quests_progress_format,
                        quest.progress,
                        quest.target,
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(color = AccessDefaults.TextMuted),
                )
            }
        }
    }
}

@Composable
private fun QuestPhotoThumb(
    photo: EventMemory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val thumbShape = RoundedCornerShape(11.dp)
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(thumbShape)
            .background(AccessDefaults.SurfaceElevated)
            .clickable(onClick = onClick),
    ) {
        BaseImage(
            imageUrl = photo.imageUrl,
            contentScale = ContentScale.Crop,
            cacheKey = photo.id,
            showLoadingIndicator = true,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun EventMemory.captionLabel(): String = when {
    !isMine -> stringResource(Res.string.quests_photo_shot_by_format, ownerName)
    tagged.isNotEmpty() ->
        stringResource(Res.string.quests_photo_with_format, tagged.joinToString(", ") { it.fullName })

    else -> stringResource(Res.string.quests_photo_shot_by_you)
}
