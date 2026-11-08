package com.kaanf.core.designsystem.profileboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.profileboard.component.boardTileClickable
import com.kaanf.core.designsystem.profileboard.component.BoardAskTile
import com.kaanf.core.designsystem.profileboard.component.BoardBackdrop
import com.kaanf.core.designsystem.profileboard.component.BoardLovesRow
import com.kaanf.core.designsystem.profileboard.component.BoardMovieTile
import com.kaanf.core.designsystem.profileboard.component.BoardSongTile
import com.kaanf.core.designsystem.profileboard.component.BoardToolSheet
import com.kaanf.core.designsystem.profileboard.component.BoardPlaceholderTile
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
    controls: (@Composable () -> Unit)? = null,
    qrModifier: Modifier = Modifier,
    footer: @Composable () -> Unit = {},
) {
    val canEdit = state.canEdit && !state.isLoading && !state.hasLoadError && !state.hasConflict
    val editing = state.isEditingBoard
    val interactive = canEdit && !state.isSaving && !state.movieSearch.isSaving
    val editOnLongPress: (() -> Unit)? = if (interactive && !editing) {
        { onAction(ProfileBoardAction.EditBoard) }
    } else null
    val openTool: (BoardTool) -> Unit = { tool ->
        if (!editing) onAction(ProfileBoardAction.EditBoard)
        onAction(ProfileBoardAction.OpenBoardTool(tool))
    }
    val frameAlpha by animateFloatAsState(if (editing) 1f else 0f, label = "board-frame")
    val scrollState = rememberScrollState()
    val isScrolled by remember { derivedStateOf { scrollState.value > 0 } }
    val dividerAlpha = animateFloatAsState(
        targetValue = if (isScrolled) 1f else 0f,
        animationSpec = tween(150),
        label = "board-controls-divider",
    )
    val dividerColor = AccessDefaults.BorderSoft

    Box(modifier.fillMaxSize()) {
        BoardBackdrop(Modifier.matchParentSize())
        Column(Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
            if (controls != null) controls() else ProfileBoardControls(
                modifier = Modifier.padding(horizontal = 20.dp),
                onClose = onBack,
                onEditBoard = { onAction(if (editing) ProfileBoardAction.FinishEditing else ProfileBoardAction.EditBoard) },
                isEditing = editing,
                isSaving = state.isSaving || state.movieSearch.isSaving,
                onSettings = onEditProfile,
                canEdit = canEdit,
                onCancel = { onAction(ProfileBoardAction.CancelEditing) },
            )
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth()
                    .drawWithContent {
                        drawContent()
                        val thickness = 1.dp.toPx()
                        drawLine(
                            color = dividerColor,
                            start = Offset(0f, thickness / 2f),
                            end = Offset(size.width, thickness / 2f),
                            strokeWidth = thickness,
                            alpha = dividerAlpha.value,
                        )
                    }
                    .verticalScroll(scrollState)
                    .padding(start = 10.dp, end = 10.dp, top = 12.dp, bottom = contentPadding.calculateBottomPadding() + 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
                    BoardQr(state, editing, interactive, onQrImageError, editOnLongPress, qrModifier) { openTool(BoardTool.QR_SKIN) }
                    Spacer(Modifier.height(24.dp))
                    val track = state.favoriteTrack
                    if (track != null) BoardSongTile(
                        track = track,
                        onLongClick = editOnLongPress,
                        onClick = { if (editing && interactive) openTool(BoardTool.SONG) },
                        onRemove = if (editing && interactive) ({ onAction(ProfileBoardAction.RemoveSong) }) else null,
                        modifier = Modifier.fillMaxWidth().height(76.dp),
                    ) else BoardPlaceholderTile(
                        onLongClick = editOnLongPress,
                        emoji = BoardTool.SONG.emoji,
                        title = stringResource(Res.string.profile_board_song_title),
                        hint = stringResource(Res.string.profile_board_song_hint),
                        onClick = { openTool(BoardTool.SONG) },
                        enabled = interactive,
                        modifier = Modifier.fillMaxWidth().height(76.dp),
                    )
                    Spacer(Modifier.height(22.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        val movie = state.favoriteMovie
                        if (movie != null) BoardMovieTile(
                            movie = movie,
                            onLongClick = editOnLongPress,
                            onClick = { if (editing && interactive) openTool(BoardTool.FILM) },
                            onRemove = if (editing && interactive) ({ onAction(ProfileBoardAction.RemoveMovie) }) else null,
                            modifier = Modifier.weight(1f).height(160.dp),
                        ) else BoardPlaceholderTile(
                            onLongClick = editOnLongPress,
                            emoji = BoardTool.FILM.emoji,
                            title = stringResource(Res.string.profile_board_film_title),
                            hint = stringResource(Res.string.profile_board_film_hint),
                            onClick = { openTool(BoardTool.FILM) },
                            enabled = interactive,
                            tall = true,
                            modifier = Modifier.weight(1f).height(160.dp),
                        )
                        val askMe = state.askMe
                        if (askMe != null) BoardAskTile(
                            text = askMe,
                            onLongClick = editOnLongPress,
                            onClick = { if (editing) openTool(BoardTool.ASK) },
                            onRemove = if (editing && interactive) ({ onAction(ProfileBoardAction.RemoveAskMe) }) else null,
                            modifier = Modifier.weight(1f).height(160.dp),
                        ) else BoardPlaceholderTile(
                            onLongClick = editOnLongPress,
                            emoji = BoardTool.ASK.emoji,
                            title = stringResource(Res.string.profile_board_ask_title),
                            hint = stringResource(Res.string.profile_board_ask_hint),
                            onClick = { openTool(BoardTool.ASK) },
                            enabled = interactive,
                            tall = true,
                            modifier = Modifier.weight(1f).height(160.dp),
                        )
                    }
                    if (state.hobbies.isNotEmpty()) BoardLovesRow(
                        hobbies = state.hobbies,
                        onLongClick = editOnLongPress,
                        editing = editing && interactive,
                        onEdit = { if (editing) openTool(BoardTool.LOVES) },
                        onRemove = { onAction(ProfileBoardAction.RemoveHobby(it)) },
                        modifier = Modifier.padding(top = 22.dp).fillMaxWidth(),
                    )
                    AnimatedVisibility(
                        visible = state.hobbies.isEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        BoardPlaceholderTile(
                            onLongClick = editOnLongPress,
                            emoji = BoardTool.LOVES.emoji,
                            title = stringResource(Res.string.profile_board_loves_title),
                            hint = stringResource(Res.string.profile_board_loves_hint),
                            onClick = { openTool(BoardTool.LOVES) },
                            enabled = interactive,
                            modifier = Modifier.padding(top = 22.dp).fillMaxWidth().height(60.dp),
                        )
                    }
                }
                footer()
                if (!editing && state.hasLoadError) {
                    TextButton(onClick = onRetry) { Text(stringResource(Res.string.profile_board_retry)) }
                }
                if (!editing && state.hasConflict) {
                    TextButton(onClick = { onAction(ProfileBoardAction.ReloadBoard) }) {
                        Text(stringResource(Res.string.profile_board_reload))
                    }
                }
            }
        }
        BoardToolSheet(state, onAction)
    }
}

@Composable
private fun BoardQr(
    state: ProfileBoardState,
    editing: Boolean,
    enabled: Boolean,
    onQrImageError: () -> Unit,
    onLongClick: (() -> Unit)?,
    qrModifier: Modifier,
    onChangeSkin: () -> Unit,
) {
    Box(contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = qrModifier.size(150.dp).clip(QrShape).background(AccessDefaults.Background)
                .border(1.dp, AccessDefaults.TextPrimary.copy(alpha = .14f), QrShape)
                .boardTileClickable(enabled = enabled, onLongClick = onLongClick,
                    onClick = { if (editing) onChangeSkin() }),
        ) {
            if (state.isLoading) ProfileBoardQrLoading(Modifier.matchParentSize())
            else ProfileBoardQrArtwork(
                publicCode = state.publicCode,
                qrStyleId = state.qrStyleId,
                qrRender = state.qrRender,
                // While editing, a newly picked theme shows its preview until the save renders it.
                isEditing = editing,
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
