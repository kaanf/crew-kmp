package com.kaanf.game.presentation.session.phase

import androidx.compose.ui.text.style.TextAlign
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import androidx.compose.runtime.saveable.rememberSaveable
import crew.feature.game.presentation.generated.resources.qr_home_how_to_play
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter
import org.jetbrains.compose.resources.painterResource
import crew.feature.game.presentation.generated.resources.qr_home_how_it_works
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.coachmark.coachmarkTarget
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
import com.kaanf.game.presentation.session.component.GameBottomBarReservedHeight
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.leaderboard_points_format
import crew.feature.game.presentation.generated.resources.match_phase_qr_home_description
import crew.feature.game.presentation.generated.resources.match_phase_qr_home_scan_action
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun QrHomePhase(
    state: MatchSessionState,
    onAction: (MatchSessionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val permissionController = rememberPermissionController()
    val scope = rememberCoroutineScope()
    var showCameraPermissionDialog by remember { mutableStateOf(false) }
    var showHowToPlay by rememberSaveable { mutableStateOf(false) }

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
    if (showHowToPlay) {
        ContainerBottomSheet(onDismiss = { showHowToPlay = false }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Image(
                    painter = painterResource(Res.drawable.qr_home_how_it_works),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(AccessDefaults.TextPrimary),
                    modifier = Modifier.size(width = 120.dp, height = 56.dp),
                )
                Text(
                    text = stringResource(Res.string.qr_home_how_to_play),
                    style = MaterialTheme.typography.headlineLarge,
                    color = AccessDefaults.TextPrimary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(Res.string.match_phase_qr_home_description),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = AccessDefaults.TextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
    com.kaanf.core.designsystem.profileboard.ProfileBoardTilesContent(
        state = com.kaanf.core.designsystem.profileboard.ProfileBoardState(
            publicCode = state.publicCode.ifBlank { state.matchQrToken.orEmpty() },
            fullName = state.currentUserName.orEmpty(),
            profilePictureUrl = state.currentUserPhotoUrl,
            qrStyleId = state.profileBoard.qrStyleId,
            qrRender = state.qrRender,
            qr = state.profileBoard.qr,
            hobbies = state.profileBoard.hobbies,
            favoriteTrack = state.favoriteTrack,
            favoriteMovie = state.favoriteMovie,
            isLoading = false,
        ),
        modifier = modifier.fillMaxSize().padding(bottom = GameBottomBarReservedHeight),
        onBack = { onAction(MatchSessionAction.OnBackClick) },
        onEditProfile = {},
        onAction = {},
        controls = {
            com.kaanf.core.designsystem.profileboard.component.ProfileBoardControls(
                onClose = { onAction(MatchSessionAction.OnBackClick) },
                onEditBoard = { showHowToPlay = true },
                actionLabel = stringResource(Res.string.qr_home_how_to_play),
                showSettings = false,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        },
        qrModifier = Modifier.coachmarkTarget(GameCoachmarkKey.Qr),
        footer = {
            state.announcementBody?.let { body ->
                AnnouncementChip(
                    body = body,
                    endsAtEpochMillis = state.announcementEndsAtEpochMillis,
                    onClick = if (state.announcementCocktail != null) {
                        { onAction(MatchSessionAction.OnAnnouncementClicked) }
                    } else null,
                )
            }
            Text(
                text = stringResource(Res.string.leaderboard_points_format, state.currentUserScore),
                modifier = Modifier.coachmarkTarget(GameCoachmarkKey.Score),
                style = MaterialTheme.typography.labelLarge,
                color = AccessDefaults.Accent,
            )
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
        },
    )
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
