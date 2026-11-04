package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.kaanf.core.designsystem.profileboard.ProfileBoardAction
import com.kaanf.core.domain.model.profileboard.BoardLayout
import com.kaanf.core.domain.model.profileboard.BoardSticker
import com.kaanf.core.domain.model.profileboard.BoardStickerPlacement
import kotlin.math.roundToInt

/** Draws sticker transforms in the same canonical board coordinates on both screens. */
@Composable
internal fun ProfileBoardStickerLayer(
    stickers: List<BoardSticker>,
    layout: BoardLayout,
    isEditing: Boolean,
    canStartEditing: Boolean = false,
    focusedStickerSlot: Int?,
    hiddenSlot: Int? = null,
    onAction: (ProfileBoardAction) -> Unit,
    onDraggingChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        val width = maxWidth
        val widthPx = with(LocalDensity.current) { width.toPx() }
        var draggingSlot by remember { mutableStateOf<Int?>(null) }
        stickers.forEach { sticker ->
            key(sticker.slot) {
                val (stickerWidth, stickerHeight) = BoardStickerPlacement.footprint(sticker, layout)
                DraggableBoardSticker(
                    sticker = sticker,
                    isEditing = isEditing,
                    canStartEditing = canStartEditing,
                    isFocused = focusedStickerSlot == sticker.slot,
                    footprintWidth = width * stickerWidth,
                    footprintHeight = width * stickerHeight,
                    artworkWidth = width * (sticker.design.width * layout.stickerScale * sticker.scale),
                    artworkHeight = width * (sticker.design.height * layout.stickerScale * sticker.scale),
                    onAction = onAction,
                    onDraggingChanged = { dragging ->
                        draggingSlot = if (dragging) sticker.slot else null
                        onDraggingChanged(dragging)
                    },
                    modifier = Modifier.alpha(if (hiddenSlot == sticker.slot) 0f else 1f)
                        .absoluteOffset {
                            IntOffset(
                                ((sticker.position.x - stickerWidth / 2f) * widthPx).roundToInt(),
                                ((sticker.position.y - stickerHeight / 2f) * widthPx).roundToInt(),
                            )
                        }
                        .size(width * stickerWidth, width * stickerHeight)
                        .zIndex(if (draggingSlot == sticker.slot || focusedStickerSlot == sticker.slot) 2f else 1f),
                )
            }
        }
    }
}
