package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import crew.feature.game.presentation.generated.resources.qr_home_quests_paper
import crew.feature.game.presentation.generated.resources.qr_home_passport_paper
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import com.kaanf.game.presentation.session.component.GameBottomBarReservedHeight
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
            .padding(top = 8.dp, bottom = GameBottomBarReservedHeight),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(Res.string.match_phase_qr_home_title))
                    append("\u00A0")
                    appendInlineContent("how-to-play", "ⓘ")
                },
                inlineContent = mapOf(
                    "how-to-play" to InlineTextContent(
                        Placeholder(28.sp, 28.sp, PlaceholderVerticalAlign.Center),
                    ) {
                        IconButton(
                            onClick = { showHowToPlay = true },
                            modifier = Modifier.size(28.dp),
                        ) {
                            Icon(
                                painter = painterResource(AccessIcons.Info),
                                contentDescription = stringResource(Res.string.qr_home_how_to_play),
                                tint = AccessDefaults.TextSecondary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    },
                ),
                modifier = Modifier.weight(1f).semantics { heading() },
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    lineHeight = 30.sp,
                    letterSpacing = (-0.8).sp,
                ),
                color = AccessDefaults.TextPrimary,
            )
            Image(
                painter = painterResource(Res.drawable.qr_home_how_it_works),
                contentDescription = null,
                colorFilter = ColorFilter.tint(AccessDefaults.TextPrimary),
                modifier = Modifier.size(width = 80.dp, height = 64.dp),
            )
        }

        BoxWithConstraints(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            // Reserve card padding, identity, button, quick links and three 16dp gaps.
            val qrSize = minOf(
                300.dp,
                (maxWidth - 32.dp).coerceAtLeast(0.dp),
                (maxHeight - 228.dp).coerceAtLeast(0.dp),
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .background(AccessDefaults.Surface, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PlayerIdentity(state = state)
                    UserQrCard(
                        inputText = state.matchQrToken.orEmpty(),
                        foregroundColor = AccessDefaults.TextPrimary,
                        qrContentDescription = stringResource(Res.string.match_phase_qr_home_qr_label),
                        modifier = Modifier.size(qrSize).coachmarkTarget(GameCoachmarkKey.Qr),
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
            }
        }
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
private fun PlayerIdentity(state: MatchSessionState) {
    Row(
        modifier = Modifier.fillMaxWidth()
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
            avatarSize = 36,
            textSize = 16.0,
        )
        Text(
            text = state.currentUserName.orEmpty(),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AccessDefaults.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(Res.string.leaderboard_points_format, state.currentUserScore),
            modifier = Modifier
                .background(AccessDefaults.SurfaceElevated, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = AccessDefaults.Accent,
        )
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
