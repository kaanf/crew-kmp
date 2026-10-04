package com.kaanf.game.presentation.session.phase

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.game.presentation.session.component.TASK_PHOTO_OPEN_ANIM_MS
import com.kaanf.game.presentation.session.component.TaskPhotoCapture
import com.kaanf.game.presentation.session.component.TaskPhotoStatusChip
import crew.feature.game.presentation.generated.resources.match_task_photo_sent_label
import crew.feature.game.presentation.generated.resources.match_task_photo_take_action
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.random.Random
import androidx.compose.ui.draw.rotate
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.dottedBorder
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory
import com.kaanf.core.designsystem.component.card.GradientChallengeCard
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_onboarding_info_text
import crew.feature.game.presentation.generated.resources.match_phase_loser_active_subtitle
import crew.feature.game.presentation.generated.resources.match_phase_loser_active_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoserActiveTaskPhase(
    opponentName: String,
    task: GameTask?,
    modifier: Modifier = Modifier,
    opponentImageUrl: String? = null,
    isUploadingPhoto: Boolean = false,
    photoUploaded: Boolean = false,
    onPhotoCaptured: (ImageBitmap) -> Unit = {},
) {
    val isPhotoTask = task?.category == TaskCategory.Photo
    var isCapturing by remember { mutableStateOf(false) }
    val showCapture = isCapturing && !photoUploaded

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            // Kart masadan kalkar, yerine kamera "açılır". Yalnız scale/alpha (graphicsLayer):
            // relayout yok; önizleme TextureView olduğu için dönüşümleri doğru çizer.
            AnimatedContent(
                targetState = showCapture,
                transitionSpec = {
                    val duration = TASK_PHOTO_OPEN_ANIM_MS
                    (fadeIn(tween(duration, delayMillis = 60)) +
                        scaleIn(tween(duration, delayMillis = 60, easing = EaseOutBack), initialScale = 0.9f)) togetherWith
                        (fadeOut(tween(duration / 2)) + scaleOut(tween(duration / 2), targetScale = 0.92f))
                },
                contentAlignment = Alignment.Center,
                label = "task_photo_capture",
                modifier = Modifier.fillMaxSize(),
            ) { capturing ->
                if (capturing) {
                    TaskPhotoCapture(
                        isUploading = isUploadingPhoto,
                        onSend = onPhotoCaptured,
                        onCancel = { isCapturing = false },
                        modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        task?.let {
                            // Masaya atılmış oyun kartı hissi: göreve özel, okunurluğu bozmayan hafif eğim.
                            val tilt = remember(it.id) { tableTiltFor(it.id) }
                            GradientChallengeCard(
                                card = it.toUiModel(),
                                cardSize = TaskCardSize,
                                emphasized = true,
                                modifier = Modifier.rotate(tilt),
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isPhotoTask && !showCapture,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut(tween(120)) + shrinkVertically(),
        ) {
            if (photoUploaded) {
                TaskPhotoStatusChip(
                    text = stringResource(Res.string.match_task_photo_sent_label),
                    highlighted = true,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            } else {
                BaseButton(
                    text = stringResource(Res.string.match_task_photo_take_action),
                    onClick = { isCapturing = true },
                    filled = true,
                    leadingIcon = AccessIcons.Camera,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                )
            }
        }

        OpponentStatusCard(
            title = stringResource(Res.string.match_phase_loser_active_title, opponentName),
            subtitle = stringResource(Res.string.match_phase_loser_active_subtitle),
            opponentName = opponentName,
            opponentImageUrl = opponentImageUrl,
        )
    }
}

internal fun tableTiltFor(taskId: String): Float {
    val random = Random(taskId.hashCode())
    val magnitude = 2f + random.nextFloat() * 1.5f
    return if (random.nextBoolean()) magnitude else -magnitude
}

@Composable
internal fun OpponentStatusCard(
    title: String,
    subtitle: String,
    opponentName: String,
    opponentImageUrl: String? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dottedBorder(
                color = AccessDefaults.Border,
                shape = AccessShapes.Medium,
                strokeWidth = 1.dp,
                dotLength = 2.dp,
                gapLength = 4.dp,
            ),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AvatarCircle(
                    content = avatarContentFor(
                        imageUrl = opponentImageUrl,
                        initialsLabel = opponentName.take(1).uppercase().ifBlank { "?" },
                        seed = opponentName,
                    ),
                    avatarSize = 48,
                    borderSize = 2
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.TextPrimary,
                            fontSize = 15.sp
                        ),
                    )

                    Text(
                        text = subtitle,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.TextMuted,
                            fontSize = 12.sp
                        ),
                    )
                }
            }
        },
    )
}

@Composable
@Preview
private fun Preview() {
    CrewTheme {
        LoserActiveTaskPhase(
            opponentName = "Mira",
            task = GameTask(
                id = "1",
                title = "🌍 Get two strangers to teach you the same word in their language.",
                points = 20,
                category = TaskCategory.Icebreaker,
            ),
        )
    }
}

