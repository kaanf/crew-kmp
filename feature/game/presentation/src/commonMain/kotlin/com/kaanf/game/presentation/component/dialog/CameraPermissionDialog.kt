package com.kaanf.game.presentation.component.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_camera_permission_description
import crew.feature.game.presentation.generated.resources.match_camera_permission_dismiss_action
import crew.feature.game.presentation.generated.resources.match_camera_permission_settings_action
import crew.feature.game.presentation.generated.resources.match_camera_permission_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CameraPermissionDialog(
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.match_camera_permission_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center,
            ),
        )

        Text(
            text = stringResource(Res.string.match_camera_permission_description),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        BaseButton(
            text = stringResource(Res.string.match_camera_permission_settings_action),
            onClick = onOpenSettings,
            filled = true,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(Res.string.match_camera_permission_dismiss_action),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.LeftArrowColor,
                fontSize = 12.sp,
            ),
        )
    }
}

@Composable
@Preview
fun CameraPermissionDialogPreview() {
    CrewTheme {
        CameraPermissionDialog(
            onOpenSettings = {},
            onDismiss = {},
        )
    }
}
