package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.component.qr.UserQrCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.presentation.permission.Permission
import com.kaanf.core.presentation.permission.PermissionState
import com.kaanf.core.presentation.permission.rememberPermissionController
import com.kaanf.game.presentation.component.OnboardingInfoCard
import com.kaanf.game.presentation.component.dialog.CameraPermissionDialog
import com.kaanf.game.presentation.memories.TonightsRollSection
import com.kaanf.game.presentation.session.MatchSessionAction
import com.kaanf.game.presentation.session.MatchSessionState
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.leaderboard_points_format
import crew.feature.game.presentation.generated.resources.match_phase_qr_home_description
import crew.feature.game.presentation.generated.resources.match_phase_qr_home_scan_action
import crew.feature.game.presentation.generated.resources.match_you_avatar_label
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun QrHomePhase(
    state: MatchSessionState,
    onAction: (MatchSessionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val permissionController = rememberPermissionController()
    val scope = rememberCoroutineScope()
    var showCameraPermissionDialog by remember { mutableStateOf(false) }

    if (showCameraPermissionDialog) {
        BaseDialog(onDismissRequest = { showCameraPermissionDialog = false }) {
            CameraPermissionDialog(
                onOpenSettings = {
                    showCameraPermissionDialog = false
                    permissionController.openAppSettings()
                },
                onDismiss = { showCameraPermissionDialog = false },
            )
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, bottom = 72.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
            AvatarCircle(
                content = avatarContentFor(
                    imageUrl = state.currentUserPhotoUrl,
                    initialsLabel = state.currentUserName.orEmpty().take(1).uppercase()
                        .ifEmpty { stringResource(Res.string.match_you_avatar_label) },
                    seed = state.currentUserName.orEmpty(),
                ),
                avatarSize = 48,
                textSize = 24.0,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.currentUserName.orEmpty(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextPrimary,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = stringResource(
                        Res.string.leaderboard_points_format,
                        state.currentUserScore,
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextMuted,
                        fontWeight = FontWeight.Bold
                    ),
                )
            }

        UserQrCard(
            inputText = state.matchQrToken.orEmpty(),
            modifier = Modifier.fillMaxWidth(0.85f),
        )

        Text(
            text = stringResource(Res.string.match_phase_qr_home_description),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

        BaseMiniButton(
            text = stringResource(Res.string.match_phase_qr_home_scan_action),
            backgroundColor = AccessDefaults.Surface,
            onClick = {
                scope.launch {
                    when (permissionController.requestPermission(Permission.CAMERA)) {
                        PermissionState.GRANTED -> onAction(MatchSessionAction.OnScanClicked)
                        PermissionState.PERMANENTLY_DENIED -> showCameraPermissionDialog = true
                        else -> Unit
                    }
                }
            },
            filled = false,
            textColor = AccessDefaults.TextPrimary,
            leadingIcon = AccessIcons.QR,
        )

        Spacer(modifier = Modifier.height(2.dp))

        TonightsRollSection(modifier = Modifier.fillMaxWidth())

        // "Atışı kaybettin" kartı geri gelirse: GlowInfoCard(accentColor = AccessDefaults.Coral)
        // + match_phase_lost_throw_* stringleri.
    }
}

@Composable
@Preview
fun QrHomePhasePreview() {
    CrewTheme {
        QrHomePhase(
            state = MatchSessionState(),
            onAction = {}
        )
    }
}
