package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.profileboard.BoardEditorCategory
import com.kaanf.core.designsystem.profileboard.ProfileBoardAction
import com.kaanf.core.domain.model.profileboard.BoardSticker
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.profile_sticker_edit
import crew.core.designsystem.generated.resources.profile_sticker_enlarge
import crew.core.designsystem.generated.resources.profile_sticker_move_down
import crew.core.designsystem.generated.resources.profile_sticker_move_left
import crew.core.designsystem.generated.resources.profile_sticker_move_right
import crew.core.designsystem.generated.resources.profile_sticker_move_up
import crew.core.designsystem.generated.resources.profile_sticker_rotate_left
import crew.core.designsystem.generated.resources.profile_sticker_rotate_right
import crew.core.designsystem.generated.resources.profile_sticker_shrink
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DraggableBoardSticker(
    sticker: BoardSticker,
    isEditing: Boolean,
    canStartEditing: Boolean = false,
    isFocused: Boolean,
    artworkWidth: Dp,
    artworkHeight: Dp,
    footprintWidth: Dp,
    footprintHeight: Dp,
    onAction: (ProfileBoardAction) -> Unit,
    onDraggingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = if (isEditing) stringResource(Res.string.profile_sticker_edit, sticker.design.label()) else sticker.design.label()
    val moveLeft = stringResource(Res.string.profile_sticker_move_left)
    val moveRight = stringResource(Res.string.profile_sticker_move_right)
    val moveUp = stringResource(Res.string.profile_sticker_move_up)
    val moveDown = stringResource(Res.string.profile_sticker_move_down)
    val enlarge = stringResource(Res.string.profile_sticker_enlarge)
    val shrink = stringResource(Res.string.profile_sticker_shrink)
    val rotateLeft = stringResource(Res.string.profile_sticker_rotate_left)
    val rotateRight = stringResource(Res.string.profile_sticker_rotate_right)
    fun move(dx: Float, dy: Float): Boolean {
        onAction(ProfileBoardAction.MoveSticker(sticker.slot, dx, dy))
        return true
    }
    fun transform(zoom: Float, rotation: Float): Boolean {
        onAction(ProfileBoardAction.TransformSticker(sticker.slot, 0f, 0f, zoom, rotation))
        return true
    }
    val selection = if (isEditing && isFocused) {
        Modifier.border(1.dp, AccessDefaults.Accent, RoundedCornerShape(6.dp))
    } else Modifier
    Box(
        modifier = modifier.then(selection)
            .boardEditOnLongPress(enabled = canStartEditing) {
                onAction(if (isEditing) ProfileBoardAction.OpenBoardEditor else ProfileBoardAction.EditBoard)
                val category = BoardEditorCategory.entries.firstOrNull { it.stickerCategory == sticker.design.category }
                if (category != null) onAction(ProfileBoardAction.SelectEditorCategory(category))
                onAction(ProfileBoardAction.FocusSticker(sticker.slot))
            }
            .semantics(mergeDescendants = true) {
                contentDescription = description
                selected = isEditing && isFocused
                if (isEditing && isFocused) customActions = listOf(
                    CustomAccessibilityAction(moveLeft) { move(-0.03f, 0f) },
                    CustomAccessibilityAction(moveRight) { move(0.03f, 0f) },
                    CustomAccessibilityAction(moveUp) { move(0f, -0.03f) },
                    CustomAccessibilityAction(moveDown) { move(0f, 0.03f) },
                    CustomAccessibilityAction(enlarge) { transform(1.1f, 0f) },
                    CustomAccessibilityAction(shrink) { transform(0.9f, 0f) },
                    CustomAccessibilityAction(rotateLeft) { transform(1f, -15f) },
                    CustomAccessibilityAction(rotateRight) { transform(1f, 15f) },
                )
            }
            .then(if (isEditing) Modifier.clickable(role = Role.Button) {
                onAction(ProfileBoardAction.FocusSticker(sticker.slot))
            } else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        BoardStickerArtwork(sticker.design, Modifier.requiredSize(artworkWidth, artworkHeight).rotate(sticker.rotation))
        if (isEditing) {
            StickerRemoveButton(
                onClick = { onAction(ProfileBoardAction.DeleteSticker(sticker.slot)) },
                modifier = Modifier.align(Alignment.TopEnd),
            )
            if (isFocused) StickerTransformHandle(
                sticker = sticker,
                footprintWidth = footprintWidth,
                footprintHeight = footprintHeight,
                onAction = onAction,
                onTransforming = onDraggingChanged,
                modifier = Modifier.align(Alignment.BottomStart),
            )
        }
    }
}
