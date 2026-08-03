package com.kaanf.game.presentation.memories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.game.domain.model.EventMemory
import com.kaanf.game.presentation.quests.PhotoPin
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.memories_shared_by_format
import crew.feature.game.presentation.generated.resources.memories_shared_by_you
import crew.feature.game.presentation.generated.resources.memories_you_badge
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MemoryLightbox(
    memory: EventMemory,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6060503))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 360.dp)
                    .fillMaxWidth()
                    // Fotoğraf/altbilgi dokunuşları scrim'e düşüp diyaloğu kapatmasın.
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                TaggedPhoto(memory = memory)

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AvatarCircle(
                        content = avatarContentFor(
                            imageUrl = memory.ownerProfilePictureUrl,
                            initialsLabel = memory.ownerName.take(1).uppercase(),
                            seed = memory.ownerName,
                        ),
                        avatarSize = 36,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (memory.isMine) {
                                stringResource(Res.string.memories_shared_by_you)
                            } else {
                                stringResource(Res.string.memories_shared_by_format, memory.ownerName)
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                        Text(
                            text = memory.capturedAt.toClockLabel(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                            ),
                        )
                    }
                    if (memory.isMine) {
                        Text(
                            text = stringResource(Res.string.memories_you_badge),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccessDefaults.OnAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                            ),
                            modifier = Modifier
                                .background(AccessDefaults.Accent, AccessShapes.Pill)
                                .padding(horizontal = 9.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(1.dp, Color.White.copy(alpha = 0.16f), CircleShape)
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(AccessIcons.Close),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(19.dp),
                )
            }
        }
    }
}

/**
 * Fotoğraf kırpılmadan, kendi oranında çizilir: etiket pinleri 0-1 oranıyla saklandığı
 * için kutunun çizilen kareyle birebir aynı olması gerekiyor. Ölçü yükleme bitince
 * geldiğinden pinler o ana kadar çizilmez.
 */
@Composable
private fun TaggedPhoto(
    memory: EventMemory,
    modifier: Modifier = Modifier,
) {
    val photoShape = RoundedCornerShape(18.dp)
    var photoSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(photoShape)
            .background(AccessDefaults.SurfaceElevated)
            .border(width = 1.dp, color = AccessDefaults.BorderSoft, shape = photoShape)
            .onSizeChanged { photoSize = it },
    ) {
        BaseImage(
            imageUrl = memory.imageUrl,
            contentScale = ContentScale.Fit,
            cacheKey = memory.id,
            modifier = Modifier.fillMaxWidth(),
        )
        if (photoSize.height > 0) {
            val boxWidth = with(density) { photoSize.width.toDp() }
            val boxHeight = with(density) { photoSize.height.toDp() }
            memory.tagged.forEachIndexed { index, tag ->
                PhotoPin(
                    xFraction = tag.pinX,
                    yFraction = tag.pinY,
                    number = index + 1,
                    label = tag.fullName,
                    boxWidth = boxWidth,
                    boxHeight = boxHeight,
                )
            }
        }
    }
}

internal fun Instant.toClockLabel(): String {
    val time = toLocalDateTime(TimeZone.currentSystemDefault()).time
    return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}
