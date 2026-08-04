package com.kaanf.game.presentation.quests

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.permission.Permission
import com.kaanf.core.presentation.permission.PermissionState
import com.kaanf.core.presentation.permission.rememberPermissionController
import com.kaanf.core.presentation.util.mediapicker.decodeImageForCrop
import com.kaanf.game.domain.model.EventParticipant
import com.kaanf.game.presentation.component.dialog.CameraPermissionDialog
import com.kaanf.game.domain.model.Quest
import com.kaanf.game.domain.model.QuestPhotoTag
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_camera_permission_retry_action
import crew.feature.game.presentation.generated.resources.quests_photo_hint_tap_photo
import crew.feature.game.presentation.generated.resources.quests_photo_no_participants
import crew.feature.game.presentation.generated.resources.quests_photo_picker_subtitle
import crew.feature.game.presentation.generated.resources.quests_photo_picker_title
import crew.feature.game.presentation.generated.resources.quests_photo_retake_action
import crew.feature.game.presentation.generated.resources.quests_photo_selected_format
import crew.feature.game.presentation.generated.resources.quests_photo_send_action
import crew.feature.game.presentation.generated.resources.quests_photo_sending
import crew.feature.game.presentation.generated.resources.quests_photo_sheet_subtitle_format
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Sunucunun da hedeflediği uzun kenar (backend 1600px'e küçültüyor). */
private const val MAX_UPLOAD_DIMENSION = 1600

/**
 * Çekilen kareyi etiketleyip gönderme ekranı: fotoğrafta bir yere dokun → katılımcı
 * seçici açılır → pin oraya düşer. Sunucu tam olarak [Quest.requiredTags] kişi bekler ve
 * pinleri 0-1 aralığında ister, o yüzden gönder butonu sayı tutmadan açılmaz.
 *
 * Fotoğraf burada decode edilir: hem pin oranlarının dayandığı kare, hem yüklenen kare
 * aynı bitmap olsun diye (EXIF-upright + 1600px'e küçültülmüş).
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun QuestPhotoTagScreen(
    quest: Quest,
    participants: List<EventParticipant>,
    isUploading: Boolean,
    onLoadParticipants: () -> Unit,
    onBack: () -> Unit,
    onSend: (List<QuestPhotoTag>, ImageBitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    var capturedBytes by remember { mutableStateOf<ByteArray?>(null) }
    var image by remember(capturedBytes) { mutableStateOf<ImageBitmap?>(null) }
    var tags by remember(capturedBytes) { mutableStateOf(emptyList<QuestPhotoTag>()) }
    var pendingPin by remember(capturedBytes) { mutableStateOf<Pair<Float, Float>?>(null) }

    LaunchedEffect(capturedBytes) {
        image = capturedBytes?.let { decodeImageForCrop(it, maxDimension = MAX_UPLOAD_DIMENSION) }
    }

    BackHandler { onBack() }

    val scope = rememberCoroutineScope()
    val permissionController = rememberPermissionController()
    var cameraPermission by remember { mutableStateOf(PermissionState.NOT_DETERMINED) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Kutu kameraya dönüşmeden önce izin şart: actual'lar izni verilmiş kabul ediyor.
    LaunchedEffect(Unit) {
        cameraPermission = permissionController.requestPermission(Permission.CAMERA)
        if (cameraPermission == PermissionState.PERMANENTLY_DENIED) showPermissionDialog = true
    }

    if (showPermissionDialog) {
        BaseDialog(onDismissRequest = { showPermissionDialog = false }) {
            CameraPermissionDialog(
                onOpenSettings = {
                    showPermissionDialog = false
                    permissionController.openAppSettings()
                },
                onDismiss = {
                    showPermissionDialog = false
                    onBack()
                },
            )
        }
    }

    val nameOf = { participantId: String ->
        participants.firstOrNull { it.id == participantId }?.fullName.orEmpty()
    }
    val slotsLeft = quest.requiredTags - tags.size

    AppScaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                state = AppTopBarState.GameLobby(quest.title),
                onBackClick = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            if (capturedBytes == null) {
                CaptureBox(
                    isPermissionGranted = cameraPermission == PermissionState.GRANTED,
                    onCaptured = { bytes -> capturedBytes = bytes },
                    onRequestPermission = {
                        scope.launch {
                            cameraPermission = permissionController.requestPermission(Permission.CAMERA)
                            if (cameraPermission == PermissionState.PERMANENTLY_DENIED) {
                                showPermissionDialog = true
                            }
                        }
                    },
                )
            } else {
                TaggablePhoto(
                    image = image,
                    tags = tags,
                    pendingPin = pendingPin,
                    labelOf = nameOf,
                    onTapPhoto = { x, y ->
                        if (slotsLeft > 0) {
                            pendingPin = x to y
                            // Katılımcılar ancak gerçekten etiketlenecekse çekilir.
                            onLoadParticipants()
                        }
                    },
                    onRemoveTag = { tag -> tags = tags - tag },
                    onMoveTag = { tag, x, y ->
                        tags = tags.map {
                            if (it.participantId == tag.participantId) it.copy(pinX = x, pinY = y) else it
                        }
                    },
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 16.dp),
            ) {
                Text(
                    text = stringResource(
                        Res.string.quests_photo_sheet_subtitle_format,
                        quest.requiredTags,
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextMuted,
                        lineHeight = 18.sp,
                    ),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(
                        Res.string.quests_photo_selected_format,
                        tags.size,
                        quest.requiredTags,
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AccessDefaults.Accent,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier
                        .background(
                            color = AccessDefaults.Accent.copy(alpha = 0.12f),
                            shape = AccessShapes.Pill,
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }

            if (capturedBytes != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    Text(
                        text = if (slotsLeft > 0) {
                            stringResource(Res.string.quests_photo_hint_tap_photo)
                        } else {
                            ""
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = AccessDefaults.TextFaint,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = stringResource(Res.string.quests_photo_retake_action),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = AccessDefaults.TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        // Kare sıfırlanınca etiketler de gider: pinler o kareye aitti.
                        modifier = Modifier
                            .clip(AccessShapes.Pill)
                            .background(AccessDefaults.SurfaceElevated)
                            .clickable { capturedBytes = null }
                            .padding(horizontal = 13.dp, vertical = 7.dp),
                    )
                }
            }

            BaseButton(
                text = stringResource(Res.string.quests_photo_send_action),
                onClick = { image?.let { onSend(tags, it) } },
                enabled = tags.size == quest.requiredTags && image != null,
                isLoading = isUploading,
                loadingText = stringResource(Res.string.quests_photo_sending),
                filled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 24.dp),
            )
        }
    }

    val pin = pendingPin
    if (pin != null) {
        ContainerBottomSheet(onDismiss = { pendingPin = null }) {
            TagPersonPicker(
                participants = participants,
                taggedIds = tags.map { it.participantId },
                onPick = { participant ->
                    tags = tags + QuestPhotoTag(participant.id, pin.first, pin.second)
                    pendingPin = null
                },
            )
        }
    }
}

/**
 * Fotoğraf çekilene kadar aynı kutu kameradır. Oran 3:4: kamera önizlemesinin de,
 * çekilen karenin de doğal oranı, böylece çekimde kutu neredeyse hiç zıplamaz.
 */
@Composable
private fun CaptureBox(
    isPermissionGranted: Boolean,
    onCaptured: (ByteArray) -> Unit,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val boxModifier = modifier
        .fillMaxWidth()
        .aspectRatio(3f / 4f)
        .clip(RoundedCornerShape(18.dp))
        .background(AccessDefaults.SurfaceElevated)

    if (isPermissionGranted) {
        PhotoCaptureFrame(modifier = boxModifier, onCaptured = onCaptured)
        return
    }

    // İzin reddedildi: ekran çıkmaz sokak olmasın, kutunun kendisi yeniden sorar.
    Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(Res.string.match_camera_permission_retry_action),
            style = MaterialTheme.typography.labelMedium.copy(
                color = AccessDefaults.OnAccent,
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier
                .clip(AccessShapes.Pill)
                .background(AccessDefaults.Accent)
                .clickable(onClick = onRequestPermission)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}

/**
 * Kutu fotoğrafın oranına sabitlenir; böylece dokunulan noktanın oranı yüklenen
 * karedeki oranla birebir aynı olur (kırpma yok).
 */
@Composable
private fun TaggablePhoto(
    image: ImageBitmap?,
    tags: List<QuestPhotoTag>,
    pendingPin: Pair<Float, Float>?,
    labelOf: (String) -> String,
    onTapPhoto: (Float, Float) -> Unit,
    onRemoveTag: (QuestPhotoTag) -> Unit,
    onMoveTag: (QuestPhotoTag, Float, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val photoShape = RoundedCornerShape(18.dp)
    if (image == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(3f / 2f)
                .clip(photoShape)
                .background(AccessDefaults.SurfaceElevated),
        )
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(image.width.toFloat() / image.height)
            .clip(photoShape)
            .pointerInput(image) {
                detectTapGestures { offset ->
                    onTapPhoto(
                        (offset.x / size.width).coerceIn(0f, 1f),
                        (offset.y / size.height).coerceIn(0f, 1f),
                    )
                }
            },
    ) {
        Image(
            bitmap = image,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )
        tags.forEachIndexed { index, tag ->
            PhotoPin(
                xFraction = tag.pinX,
                yFraction = tag.pinY,
                number = index + 1,
                label = labelOf(tag.participantId),
                boxWidth = maxWidth,
                boxHeight = maxHeight,
                onRemove = { onRemoveTag(tag) },
                onMove = { x, y -> onMoveTag(tag, x, y) },
            )
        }
        pendingPin?.let { (x, y) ->
            Box(
                modifier = Modifier
                    .offsetPin(x, y, maxWidth, maxHeight)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(AccessDefaults.Accent.copy(alpha = 0.45f)),
            )
        }
    }
}

@Composable
private fun TagPersonPicker(
    participants: List<EventParticipant>,
    taggedIds: List<String>,
    onPick: (EventParticipant) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
    ) {
        Text(
            text = stringResource(Res.string.quests_photo_picker_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.Bold,
            ),
        )
        Text(
            text = stringResource(Res.string.quests_photo_picker_subtitle),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextMuted,
                lineHeight = 18.sp,
            ),
            modifier = Modifier.padding(top = 6.dp),
        )

        if (participants.isEmpty()) {
            Text(
                text = stringResource(Res.string.quests_photo_no_participants),
                style = MaterialTheme.typography.bodySmall.copy(color = AccessDefaults.TextFaint),
                modifier = Modifier.padding(top = 18.dp),
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .heightIn(max = 360.dp),
            ) {
                items(participants, key = { it.id }) { participant ->
                    val isTagged = participant.id in taggedIds
                    TaggablePersonRow(
                        participant = participant,
                        isTagged = isTagged,
                        onClick = { onPick(participant) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TaggablePersonRow(
    participant: EventParticipant,
    isTagged: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rowShape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(
                if (isTagged) AccessDefaults.Accent.copy(alpha = 0.1f) else AccessDefaults.SurfaceElevated,
            )
            .clickable(enabled = !isTagged, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        AvatarCircle(
            content = avatarContentFor(
                imageUrl = participant.profilePictureUrl,
                initialsLabel = participant.fullName.take(1).uppercase().ifBlank { "?" },
                seed = participant.id,
            ),
            avatarSize = 34,
        )
        Text(
            text = participant.fullName,
            style = MaterialTheme.typography.titleSmall.copy(
                color = if (isTagged) AccessDefaults.TextFaint else AccessDefaults.TextPrimary,
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier.weight(1f),
        )
        if (isTagged) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(AccessDefaults.Accent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(AccessIcons.Check),
                    contentDescription = null,
                    tint = AccessDefaults.OnAccent,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}
