package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.coachmark.coachmarkTarget
import com.kaanf.core.designsystem.component.qr.UserQrCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.presentation.permission.Permission
import com.kaanf.core.presentation.permission.PermissionState
import com.kaanf.core.presentation.permission.rememberPermissionController
import com.kaanf.game.presentation.component.AnnouncementChip
import com.kaanf.game.presentation.component.dialog.CameraPermissionDialog
import com.kaanf.game.presentation.session.MatchSessionAction
import com.kaanf.game.presentation.session.MatchSessionState
import com.kaanf.game.presentation.session.coachmark.GameCoachmarkKey
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.leaderboard_points_format
import crew.feature.game.presentation.generated.resources.match_phase_qr_home_title
import crew.feature.game.presentation.generated.resources.match_phase_qr_home_qr_label
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
            .padding(horizontal = 22.dp)
            .padding(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        state.announcementBody?.let { body ->
            AnnouncementChip(
                body = body,
                endsAtEpochMillis = state.announcementEndsAtEpochMillis,
                onClick = if (state.announcementCocktail != null) {
                    { onAction(MatchSessionAction.OnAnnouncementClicked) }
                } else {
                    null
                },
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.match_phase_qr_home_title),
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    lineHeight = 34.sp,
                    letterSpacing = (-0.8).sp,
                ),
                color = AccessDefaults.TextPrimary,
            )
            Text(
                text = stringResource(Res.string.match_phase_qr_home_description),
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                color = AccessDefaults.TextSecondary,
            )
        }

        Column(
            modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .coachmarkTarget(GameCoachmarkKey.Score),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AvatarCircle(
                    borderSize = 0,
                    content = avatarContentFor(
                        imageUrl = state.currentUserPhotoUrl,
                        initialsLabel = state.currentUserName.orEmpty().take(1).uppercase()
                            .ifEmpty { stringResource(Res.string.match_you_avatar_label) },
                        seed = state.currentUserName.orEmpty(),
                    ),
                    avatarSize = 44,
                    textSize = 18.0,
                )
                Text(
                    text = state.currentUserName.orEmpty(),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccessDefaults.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(
                        Res.string.leaderboard_points_format,
                        state.currentUserScore,
                    ),
                    modifier = Modifier
                        .background(AccessDefaults.Accent, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccessDefaults.OnAccent,
                )
            }

            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .fillMaxWidth()
                    .coachmarkTarget(GameCoachmarkKey.Qr)
                    .background(AccessDefaults.Surface, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                UserQrCard(
                    inputText = state.matchQrToken.orEmpty(),
                    qrContentDescription = stringResource(Res.string.match_phase_qr_home_qr_label),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            BaseButton(
                text = stringResource(Res.string.match_phase_qr_home_scan_action),
                modifier = Modifier.coachmarkTarget(GameCoachmarkKey.Scan),
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
                contentColor = AccessDefaults.TextPrimary,
                pill = true,
                leadingIcon = AccessIcons.QR,
            )
        }

    }
}

@Composable
@Preview(name = "QR home · compact", widthDp = 320, heightDp = 640)
@Preview(name = "QR home · phone", widthDp = 390, heightDp = 780)
fun QrHomePhasePreview() {
    CrewTheme {
        QrHomePhase(
            state = MatchSessionState(
                currentUserName = "Kaan",
                currentUserScore = 120,
                matchQrToken = "crew-preview-player",
            ),
            onAction = {}
        )
    }
}
