package com.kaanf.game.presentation.quests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.presentation.util.mediapicker.rememberCameraLauncher
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.quests_photo_open_camera_action
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * iOS'ta uygulama içi önizleme henüz yok: kutu, sistem kamerasını açan bir düğme gösterir
 * ve çekilen kare aynı kutuya döner — ekran değişmez, kamera yalnızca üstüne modal gelir.
 * AVFoundation ile gerçek önizleme yazılırsa değişecek tek yer burasıdır.
 */
@Composable
actual fun PhotoCaptureFrame(
    modifier: Modifier,
    onCaptured: (ByteArray) -> Unit,
) {
    val cameraLauncher = rememberCameraLauncher { picked -> onCaptured(picked.bytes) }

    Column(
        modifier = modifier
            .background(AccessDefaults.SurfaceElevated)
            .clickable { cameraLauncher.launch() },
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .background(AccessDefaults.Accent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(AccessIcons.Camera),
                contentDescription = null,
                tint = AccessDefaults.OnAccent,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = stringResource(Res.string.quests_photo_open_camera_action),
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextPrimary,
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    }
}
