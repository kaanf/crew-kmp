package com.kaanf.core.designsystem.profileboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.info.ReminderCard
import com.kaanf.core.designsystem.profileboard.component.BoardBackdrop
import com.kaanf.core.designsystem.profileboard.component.BoardPlaceholderTile
import com.kaanf.core.designsystem.profileboard.component.BoardToolBar
import com.kaanf.core.designsystem.profileboard.component.ProfileBoardControls
import com.kaanf.core.designsystem.profileboard.component.ProfileBoardIdentity
import com.kaanf.core.designsystem.profileboard.component.ProfileBoardIdentityLoading
import com.kaanf.core.designsystem.profileboard.component.ProfileBoardQrArtwork
import com.kaanf.core.designsystem.profileboard.component.ProfileBoardQrLoading
import com.kaanf.core.designsystem.profileboard.component.dashedBorder
import com.kaanf.core.designsystem.profileboard.component.emoji
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.Inter
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.profile_board_ask_hint
import crew.core.designsystem.generated.resources.profile_board_ask_title
import crew.core.designsystem.generated.resources.profile_board_change_skin
import crew.core.designsystem.generated.resources.profile_board_empty_body
import crew.core.designsystem.generated.resources.profile_board_empty_title
import crew.core.designsystem.generated.resources.profile_board_film_hint
import crew.core.designsystem.generated.resources.profile_board_film_title
import crew.core.designsystem.generated.resources.profile_board_loves_hint
import crew.core.designsystem.generated.resources.profile_board_loves_title
import crew.core.designsystem.generated.resources.profile_board_reload
import crew.core.designsystem.generated.resources.profile_board_retry
import crew.core.designsystem.generated.resources.profile_board_song_hint
import crew.core.designsystem.generated.resources.profile_board_song_title
import org.jetbrains.compose.resources.stringResource

private val QrShape = RoundedCornerShape(16.dp)

/**
 * The owner's tile board. Tiles have no saved content yet, so every board renders the
 * new-profile layout; [ProfileBoardAction.OpenBoardTool] is the hook for each tool's sheet.
 */
@Composable
fun ProfileBoardTilesContent(
    state: ProfileBoardState,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onAction: (ProfileBoardAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onRetry: () -> Unit = {},
    onQrImageError: () -> Unit = {},
) {
    val canEdit = state.canEdit && !state.isLoading && !state.hasLoadError && !state.hasConflict
    val editing = state.isEditingBoard
    val interactive = canEdit && !state.isSaving
    val openTool: (BoardTool) -> Unit = { tool ->
        onAction(if (editing) ProfileBoardAction.OpenBoardTool(tool) else ProfileBoardAction.EditBoard)
    }
    val frameAlpha by animateFloatAsState(if (editing) 1f else 0f, label = "board-frame")

    Box(modifier.fillMaxSize()) {
        BoardBackdrop(Modifier.matchParentSize())
        Column(Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
            ProfileBoardControls(
                modifier = Modifier.padding(horizontal = 20.dp),
                onClose = onBack,
                onEditBoard = { onAction(if (editing) ProfileBoardAction.FinishEditing else ProfileBoardAction.EditBoard) },
                isEditing = editing,
                isSaving = state.isSaving,
                onSettings = onEditProfile,
                canEdit = canEdit,
                onCancel = { onAction(ProfileBoardAction.CancelEditing) },
            )
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(start = 10.dp, end = 10.dp, top = 12.dp, bottom = (if (editing) 0.dp else contentPadding.calculateBottomPadding()) + 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedVisibility(
                    visible = !editing && canEdit,
                    modifier = Modifier.widthIn(max = 420.dp),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    // Every board is empty until tiles can be saved; the nudge leads into editing.
                    ReminderCard(
                        emoji = "✨",
                        title = stringResource(Res.string.profile_board_empty_title),
                        description = stringResource(Res.string.profile_board_empty_body),
                        onClick = { onAction(ProfileBoardAction.EditBoard) },
                        enabled = interactive,
                        modifier = Modifier.padding(horizontal = 6.dp).padding(bottom = 20.dp)
                            .border(1.dp, AccessDefaults.Border, RoundedCornerShape(16.dp)),
                    )
                }
                AnimatedVisibility(
                    visible = !editing,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    val identityModifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    if (state.isLoading && state.fullName.isBlank()) ProfileBoardIdentityLoading(identityModifier)
                    else ProfileBoardIdentity(state.fullName, state.profilePictureUrl, identityModifier, avatarSize = 64)
                }
                Column(
                    modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth()
                        .dashedBorder(AccessDefaults.TextPrimary.copy(alpha = .6f), cornerRadius = 24.dp,
                            alpha = { frameAlpha })
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    BoardQr(state, editing, interactive, onQrImageError) { openTool(BoardTool.QR_SKIN) }
                    Spacer(Modifier.height(24.dp))
                    BoardPlaceholderTile(
                        emoji = BoardTool.SONG.emoji,
                        title = stringResource(Res.string.profile_board_song_title),
                        hint = stringResource(Res.string.profile_board_song_hint),
                        onClick = { openTool(BoardTool.SONG) },
                        enabled = interactive,
                        modifier = Modifier.fillMaxWidth().height(76.dp),
                    )
                    Spacer(Modifier.height(22.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        BoardPlaceholderTile(
                            emoji = BoardTool.FILM.emoji,
                            title = stringResource(Res.string.profile_board_film_title),
                            hint = stringResource(Res.string.profile_board_film_hint),
                            onClick = { openTool(BoardTool.FILM) },
                            enabled = interactive,
                            tall = true,
                            modifier = Modifier.weight(1f).height(160.dp),
                        )
                        BoardPlaceholderTile(
                            emoji = BoardTool.ASK.emoji,
                            title = stringResource(Res.string.profile_board_ask_title),
                            hint = stringResource(Res.string.profile_board_ask_hint),
                            onClick = { openTool(BoardTool.ASK) },
                            enabled = interactive,
                            tall = true,
                            modifier = Modifier.weight(1f).height(160.dp),
                        )
                    }
                    AnimatedVisibility(
                        visible = editing,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        BoardPlaceholderTile(
                            emoji = BoardTool.LOVES.emoji,
                            title = stringResource(Res.string.profile_board_loves_title),
                            hint = stringResource(Res.string.profile_board_loves_hint),
                            onClick = { openTool(BoardTool.LOVES) },
                            enabled = interactive,
                            modifier = Modifier.padding(top = 22.dp).fillMaxWidth().height(60.dp),
                        )
                    }
                }
                if (!editing && state.hasLoadError) {
                    TextButton(onClick = onRetry) { Text(stringResource(Res.string.profile_board_retry)) }
                }
                if (!editing && state.hasConflict) {
                    TextButton(onClick = { onAction(ProfileBoardAction.ReloadBoard) }) {
                        Text(stringResource(Res.string.profile_board_reload))
                    }
                }
            }
            AnimatedVisibility(
                visible = editing,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    BoardToolBar(
                        onToolClick = { onAction(ProfileBoardAction.OpenBoardTool(it)) },
                        enabled = interactive,
                        modifier = Modifier.widthIn(max = 420.dp)
                            .padding(horizontal = 16.dp)
                            .padding(top = 12.dp, bottom = contentPadding.calculateBottomPadding() + 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun BoardQr(
    state: ProfileBoardState,
    editing: Boolean,
    enabled: Boolean,
    onQrImageError: () -> Unit,
    onChangeSkin: () -> Unit,
) {
    Box(contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier.size(150.dp).clip(QrShape).background(AccessDefaults.Background)
                .border(1.dp, AccessDefaults.TextPrimary.copy(alpha = .14f), QrShape),
        ) {
            if (state.isLoading) ProfileBoardQrLoading(Modifier.matchParentSize())
            else ProfileBoardQrArtwork(
                publicCode = state.publicCode,
                qrStyleId = state.qrStyleId,
                qrRender = state.qrRender,
                isEditing = false,
                isSaving = state.isSaving,
                onQrImageError = onQrImageError,
                qrPadding = 12.dp,
            )
        }
        AnimatedVisibility(
            visible = editing,
            modifier = Modifier.offset(y = 13.dp),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Text(
                text = stringResource(Res.string.profile_board_change_skin),
                modifier = Modifier.clip(AccessShapes.Pill)
                    .background(AccessDefaults.Background.copy(alpha = .7f))
                    .clickable(enabled = enabled, role = Role.Button, onClick = onChangeSkin)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 10.sp,
                    letterSpacing = 1.sp),
                color = AccessDefaults.TextPrimary,
            )
        }
    }
}
