package com.kaanf.game.presentation.session.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.presentation.permission.Permission
import com.kaanf.core.presentation.permission.PermissionState
import com.kaanf.core.presentation.permission.rememberPermissionController
import com.kaanf.core.presentation.util.mediapicker.decodeImageForCrop
import com.kaanf.game.presentation.component.camera.PhotoCaptureFrame
import com.kaanf.game.presentation.component.dialog.CameraPermissionDialog
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_camera_permission_retry_action
import crew.feature.game.presentation.generated.resources.match_task_photo_retake_action
import crew.feature.game.presentation.generated.resources.match_task_photo_send_action
import crew.feature.game.presentation.generated.resources.match_task_photo_sending
import crew.feature.game.presentation.generated.resources.match_task_photo_permission_description
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Sunucunun da hedeflediği uzun kenar (backend 1600px'e küçültüyor). */
private const val MAX_UPLOAD_DIMENSION = 1600

/** Kartın kameraya dönüşme süresi; kamera bu bitince bağlanır. */
internal const val TASK_PHOTO_OPEN_ANIM_MS = 320

/**
 * PHOTO görevinin uygulama içi kamerası: kutu önce kameradır, çekimden sonra aynı kutuda
 * önizleme + Retake/Send. Kare burada decode edilir (EXIF-upright, 1600px), yüklenen de o.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TaskPhotoCapture(
    isUploading: Boolean,
    onSend: (ImageBitmap) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var capturedBytes by remember { mutableStateOf<ByteArray?>(null) }
    var image by remember(capturedBytes) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(capturedBytes) {
        image = capturedBytes?.let { decodeImageForCrop(it, maxDimension = MAX_UPLOAD_DIMENSION) }
    }

    BackHandler(enabled = !isUploading) { onCancel() }

    val scope = rememberCoroutineScope()
    val permissionController = rememberPermissionController()
    var cameraPermission by remember { mutableStateOf(PermissionState.NOT_DETERMINED) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Kutu kameraya dönüşmeden önce izin şart: actual'lar izni verilmiş kabul ediyor.
    // Açılış animasyonu bitmeden başlamaz: CameraX bind'ı ana thread'de kare düşürüyordu,
    // izin sistemi diyaloğu da animasyonun ortasında açılmasın.
    LaunchedEffect(Unit) {
        delay(TASK_PHOTO_OPEN_ANIM_MS.toLong())
        cameraPermission = permissionController.requestPermission(Permission.CAMERA)
        if (cameraPermission == PermissionState.PERMANENTLY_DENIED) showPermissionDialog = true
    }

    if (showPermissionDialog) {
        BaseDialog(onDismissRequest = { showPermissionDialog = false }) {
            CameraPermissionDialog(
                description = stringResource(Res.string.match_task_photo_permission_description),
                onOpenSettings = {
                    showPermissionDialog = false
                    permissionController.openAppSettings()
                },
                onDismiss = {
                    showPermissionDialog = false
                    onCancel()
                },
            )
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 3:4: kamera önizlemesinin de çekilen karenin de doğal oranı; çekimde kutu zıplamaz.
        val boxModifier = Modifier
            .weight(1f, fill = false)
            .aspectRatio(3f / 4f, matchHeightConstraintsFirst = true)
            .clip(RoundedCornerShape(18.dp))
            .background(AccessDefaults.SurfaceElevated)

        val captured = image
        when {
            capturedBytes != null -> Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
                if (captured == null) {
                    CircularProgressIndicator(color = AccessDefaults.Accent, strokeWidth = 2.dp)
                } else {
                    Image(
                        bitmap = captured,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize(),
                    )
                }
            }

            cameraPermission == PermissionState.GRANTED ->
                PhotoCaptureFrame(modifier = boxModifier, onCaptured = { capturedBytes = it })

            // Animasyon/izin sürerken boş kutu; "Allow camera" yanıp sönmesin.
            cameraPermission == PermissionState.NOT_DETERMINED -> Box(modifier = boxModifier)

            // İzin reddedildi: çıkmaz sokak olmasın, kutunun kendisi yeniden sorar.
            else -> Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
                PillAction(
                    text = stringResource(Res.string.match_camera_permission_retry_action),
                    filled = true,
                    onClick = {
                        scope.launch {
                            cameraPermission = permissionController.requestPermission(Permission.CAMERA)
                            if (cameraPermission == PermissionState.PERMANENTLY_DENIED) {
                                showPermissionDialog = true
                            }
                        }
                    },
                )
            }
        }

        if (capturedBytes != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                PillAction(
                    text = stringResource(Res.string.match_task_photo_retake_action),
                    filled = false,
                    onClick = { if (!isUploading) capturedBytes = null },
                )
                BaseButton(
                    text = stringResource(Res.string.match_task_photo_send_action),
                    onClick = { captured?.let(onSend) },
                    enabled = captured != null,
                    isLoading = isUploading,
                    loadingText = stringResource(Res.string.match_task_photo_sending),
                    filled = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** Görev ekranlarındaki küçük foto durumu satırı (ikon + metin, tıklanabilir olabilir). */
@Composable
fun TaskPhotoStatusChip(
    text: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val color = if (highlighted) AccessDefaults.Accent else AccessDefaults.TextMuted
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        modifier = modifier
            .clip(AccessShapes.Pill)
            .background(color.copy(alpha = 0.12f))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Icon(
            painter = painterResource(AccessIcons.Camera),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(color = color, fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun PillAction(
    text: String,
    filled: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            color = if (filled) AccessDefaults.OnAccent else AccessDefaults.TextSecondary,
            fontWeight = FontWeight.SemiBold,
        ),
        modifier = Modifier
            .clip(AccessShapes.Pill)
            .background(if (filled) AccessDefaults.Accent else AccessDefaults.SurfaceElevated)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}
