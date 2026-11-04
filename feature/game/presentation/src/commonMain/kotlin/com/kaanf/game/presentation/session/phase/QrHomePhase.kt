package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import crew.feature.game.presentation.generated.resources.qr_home_quests_paper
import crew.feature.game.presentation.generated.resources.qr_home_passport_paper
import androidx.compose.runtime.saveable.rememberSaveable
import crew.feature.game.presentation.generated.resources.qr_home_how_to_play
import org.jetbrains.compose.resources.DrawableResource
import crew.feature.game.presentation.generated.resources.qr_home_quests_title
import crew.feature.game.presentation.generated.resources.qr_home_passport_title
import crew.feature.game.presentation.generated.resources.qr_home_quests_progress
import crew.feature.game.presentation.generated.resources.qr_home_passport_progress
import crew.feature.game.presentation.generated.resources.qr_home_quests_hint
import crew.feature.game.presentation.generated.resources.qr_home_passport_hint
import crew.feature.game.presentation.generated.resources.qr_home_claim_ready
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter
import org.jetbrains.compose.resources.painterResource
import crew.feature.game.presentation.generated.resources.qr_home_how_it_works
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
    onQuestsClick: () -> Unit = {},
    onPassportClick: () -> Unit = {},
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
    Column(modifier = modifier.fillMaxSize().padding(bottom = GameBottomBarReservedHeight)) {
        state.announcementBody?.let { body ->
            AnnouncementChip(
                body = body,
                endsAtEpochMillis = state.announcementEndsAtEpochMillis,
                onClick = if (state.announcementCocktail != null) {
                    { onAction(MatchSessionAction.OnAnnouncementClicked) }
                } else null,
            )
        }
        com.kaanf.core.designsystem.profileboard.ProfileBoardContent(
            state = com.kaanf.core.designsystem.profileboard.ProfileBoardState(
                publicCode = state.publicCode.ifBlank { state.matchQrToken.orEmpty() },
                fullName = state.currentUserName.orEmpty(),
                profilePictureUrl = state.currentUserPhotoUrl,
                qrStyleId = state.profileBoard.qrStyleId,
                qrRender = state.qrRender,
                qr = state.profileBoard.qr,
                stickers = state.profileBoard.stickers,
                isLoading = false,
            ),
            qrModifier = Modifier.coachmarkTarget(GameCoachmarkKey.Qr),
            footerReservedHeight = 210.dp,
            modifier = Modifier.fillMaxSize(),
            footer = {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    GameQuickLink(
                        title = stringResource(Res.string.qr_home_quests_title),
                        subtitle = when {
                            state.questsClaimable -> stringResource(Res.string.qr_home_claim_ready)
                            state.questsCompleted != null && state.questsTotal != null ->
                                stringResource(Res.string.qr_home_quests_progress, state.questsCompleted, state.questsTotal)
                            else -> stringResource(Res.string.qr_home_quests_hint)
                        },
                        paper = Res.drawable.qr_home_quests_paper,
                        highlighted = state.questsClaimable,
                        onClick = onQuestsClick,
                        modifier = Modifier.weight(1f),
                    )
                    GameQuickLink(
                        title = stringResource(Res.string.qr_home_passport_title),
                        subtitle = when {
                            state.passportClaimable -> stringResource(Res.string.qr_home_claim_ready)
                            state.passportStampCount != null ->
                                stringResource(Res.string.qr_home_passport_progress, state.passportStampCount)
                            else -> stringResource(Res.string.qr_home_passport_hint)
                        },
                        paper = Res.drawable.qr_home_passport_paper,
                        highlighted = state.passportClaimable,
                        onClick = onPassportClick,
                        modifier = Modifier.weight(1f),
                    )
                }
                androidx.compose.material3.TextButton(onClick = { showHowToPlay = true }) {
                    Text(stringResource(Res.string.qr_home_how_to_play))
                }
            },
        )
    }
}

@Composable
private fun GameQuickLink(
    title: String,
    subtitle: String,
    paper: DrawableResource,
    highlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .heightIn(min = 60.dp),
    ) {
        Image(
            painter = painterResource(paper),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize(),
        )
        // The right side belongs to the doodle; live labels remain independent of the asset.
        Column(
            modifier = Modifier.fillMaxWidth(0.72f)
                .padding(start = 10.dp, top = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AccessDefaults.TextPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = if (highlighted) AccessDefaults.Accent else AccessDefaults.TextPrimary,
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
