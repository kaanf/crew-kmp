package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kaanf.core.domain.model.profileboard.BoardLayout
import com.kaanf.core.domain.model.profileboard.BoardRect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.*
import com.kaanf.core.domain.model.profileboard.BoardQrPlacement
import com.kaanf.core.domain.model.profileboard.BoardQrRender
import com.kaanf.core.domain.model.profileboard.BoardQrRenderStatus
import com.kaanf.core.domain.model.profileboard.BoardQrTheme
import com.kaanf.core.domain.model.profileboard.minQrSize
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.profileboard.ProfileBoardAction
import com.kaanf.core.designsystem.profileboard.ProfileBoardEditor
import com.kaanf.core.designsystem.profileboard.ProfileBoardState
import com.kaanf.core.designsystem.component.background.DottedBackground
import com.kaanf.core.domain.model.profileboard.BoardSticker
import com.kaanf.core.domain.model.profileboard.BoardStickerPlacement
import crew.core.designsystem.generated.resources.*
import crew.core.designsystem.generated.resources.profile_board_sample_qr
import org.jetbrains.compose.resources.stringResource

/** Keeps sticker guides and QR proportional on compact phones and wider screens. */
@Composable
internal fun ProfileBoardCanvas(
    publicCode: String,
    qrStyleId: String,
    qrRender: BoardQrRender?,
    qr: BoardQrPlacement,
    boardLayout: BoardLayout,
    isQrFocused: Boolean,
    isEditing: Boolean,
    isSaving: Boolean,
    onQrImageError: () -> Unit,
    canAddStickers: Boolean,
    focusedStickerSlot: Int?,
    onDraggingChanged: (Boolean) -> Unit,
    qrModifier: Modifier = Modifier,
    stickers: List<BoardSticker>,
    onAction: (ProfileBoardAction) -> Unit,
    modifier: Modifier = Modifier,
    draggedStickerSlot: Int? = null,
    onStickerDrag: (Int, Offset, Float) -> Unit = { _, _, _ -> },
    onStickerDrop: (Int) -> Boolean = { false },
    onStickerDragFinished: () -> Unit = {},
    dottedWidth: androidx.compose.ui.unit.Dp? = null,
    dottedHeight: androidx.compose.ui.unit.Dp? = null,
) {
    var canvasCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var boardWidthPx by remember { mutableStateOf(0f) }
    val contentTop = boardLayout.contentTop
    val measuredLayout = boardLayout.copy(contentTop = contentTop, qr = qr.bounds())
    val boundedQr = qr.transform(0f, 0f, 1f, contentTop, minQrSize(qrStyleId), boardLayout.height)
    val boundedLayout = measuredLayout.copy(qr = boundedQr.bounds())
    val projected = if (isEditing) ProfileBoardState(qrStyleId = qrStyleId, qr = boundedQr, boardLayout = boundedLayout,
        stickers = stickers.map { BoardStickerPlacement.previewTransform(it, 0f, 0f, 1f, 0f, boundedLayout) })
    else ProfileBoardEditor.settleBoard(ProfileBoardState(qrStyleId = qrStyleId, qr = boundedQr, boardLayout = boundedLayout, stickers = stickers))
    val visibleQr = projected.qr
    val visibleStickers = projected.stickers
    val layout = projected.boardLayout
    // Conservative bounds include each guide's rotation; never draw an add guide over a moved QR.
    val guideAreas = listOf(
        BoardRect(.06f, contentTop, .42f, contentTop + .20f),
        BoardRect(.76f, contentTop + .01f, .98f, contentTop + .23f),
        BoardRect(-.01f, layout.height - .20f, .29f, layout.height - .02f),
        BoardRect(.75f, layout.height - .24f, .99f, layout.height),
    )
    fun guideClear(slot: Int): Boolean {
        val guide = guideAreas[slot]
        val area = layout.qr
        return guide.top >= contentTop && guide.bottom <= layout.height &&
            (guide.right <= area.left - .012f || guide.left >= area.right + .012f ||
            guide.bottom <= area.top - .012f || guide.top >= area.bottom + .012f)
    }
    val qrTheme = BoardQrTheme.find(qrStyleId)
    val moveLeft = stringResource(Res.string.profile_sticker_move_left)
    val moveRight = stringResource(Res.string.profile_sticker_move_right)
    val moveUp = stringResource(Res.string.profile_sticker_move_up)
    val moveDown = stringResource(Res.string.profile_sticker_move_down)
    val enlarge = stringResource(Res.string.profile_sticker_enlarge)
    val shrink = stringResource(Res.string.profile_sticker_shrink)
    BoxWithConstraints(
        modifier = modifier.widthIn(max = 420.dp).fillMaxWidth().aspectRatio(BoardStickerPlacement.ASPECT_RATIO)
            .onGloballyPositioned {
                canvasCoordinates = it
                boardWidthPx = it.size.width.toFloat()
            }
            .focusedStickerGestures(
                enabled = isEditing,
                stickers = visibleStickers,
                focusedStickerSlot = focusedStickerSlot,
                layout = layout,
                widthPx = boardWidthPx,
                onAction = onAction,
                onTransforming = onDraggingChanged,
                onDragPosition = { slot, point -> canvasCoordinates?.let { onStickerDrag(slot, it.localToRoot(point), (it.localToRoot(Offset(1f, 0f)) - it.localToRoot(Offset.Zero)).getDistance()) } },
                onDrop = onStickerDrop,
                onDragFinished = onStickerDragFinished,
            )
            .qrBoardGestures(isEditing, visibleQr, visibleStickers, boardWidthPx, onAction, onDraggingChanged),
    ) {
        val boardWidth = maxWidth
        DottedBackground(Modifier.absoluteOffset(y = boardWidth * contentTop)
            .requiredWidth(dottedWidth ?: boardWidth)
            .height(maxOf(boardWidth * BoardStickerPlacement.HEIGHT, dottedHeight ?: 0.dp) - boardWidth * contentTop))

        Box(
            modifier = qrModifier
                .absoluteOffset(x = boardWidth * (visibleQr.x - visibleQr.size / 2), y = boardWidth * (visibleQr.y - visibleQr.size / 2))
                .size(boardWidth * visibleQr.size)
                .boardEditOnLongPress(enabled = canAddStickers) {
                    onAction(if (isEditing) ProfileBoardAction.OpenBoardEditor else ProfileBoardAction.EditBoard)
                    onAction(ProfileBoardAction.FocusQr)
                }
                .then(if (qrTheme == null) Modifier.background(AccessDefaults.Background) else Modifier)
                .then(if (isEditing && isQrFocused) Modifier.border(1.dp, AccessDefaults.Accent) else Modifier)
                .semantics(mergeDescendants = true) {
                    if (isEditing) {
                        selected = isQrFocused
                        customActions = listOf(
                            CustomAccessibilityAction(moveLeft) { onAction(ProfileBoardAction.TransformQr(-.03f, 0f, 1f)); true },
                            CustomAccessibilityAction(moveRight) { onAction(ProfileBoardAction.TransformQr(.03f, 0f, 1f)); true },
                            CustomAccessibilityAction(moveUp) { onAction(ProfileBoardAction.TransformQr(0f, -.03f, 1f)); true },
                            CustomAccessibilityAction(moveDown) { onAction(ProfileBoardAction.TransformQr(0f, .03f, 1f)); true },
                            CustomAccessibilityAction(enlarge) { onAction(ProfileBoardAction.TransformQr(0f, 0f, 1.1f)); true },
                            CustomAccessibilityAction(shrink) { onAction(ProfileBoardAction.TransformQr(0f, 0f, .9f)); true },
                        )
                    }
                },
        ) {
            ProfileBoardQrArtwork(
                publicCode = publicCode,
                qrStyleId = qrStyleId,
                qrRender = qrRender,
                isEditing = isEditing,
                isSaving = isSaving,
                onQrImageError = onQrImageError,
                qrPadding = boardWidth * visibleQr.size * (2f / 25f),
            )
        }
        if (canAddStickers && guideClear(0) && stickers.none { it.slot == 0 }) {
            ProfileBoardStickerSlot(
                onClick = { onAction(ProfileBoardAction.OpenStickerPicker(0)) },
                shape = RoundedCornerShape(50),
                modifier = Modifier.align(Alignment.TopStart)
                    .padding(start = boardWidth * 0.08f, top = boardWidth * (contentTop + .04f))
                    .size(width = boardWidth * 0.32f, height = boardWidth * 0.12f)
                    .rotate(-9f),
            )
        }
        if (canAddStickers && guideClear(1) && stickers.none { it.slot == 1 }) {
            ProfileBoardStickerSlot(
                onClick = { onAction(ProfileBoardAction.OpenStickerPicker(1)) },
                shape = CircleShape,
                modifier = Modifier.align(Alignment.TopEnd)
                    .padding(end = boardWidth * 0.02f, top = boardWidth * (contentTop + .01f))
                    .size(boardWidth * 0.22f)
                    .rotate(10f),
            )
        }
        if (canAddStickers && guideClear(2) && stickers.none { it.slot == 2 }) {
            ProfileBoardStickerSlot(
                onClick = { onAction(ProfileBoardAction.OpenStickerPicker(2)) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.BottomStart)
                    .padding(start = boardWidth * 0.01f, bottom = boardWidth * (BoardStickerPlacement.HEIGHT - layout.height + .06f))
                    .size(width = boardWidth * 0.26f, height = boardWidth * 0.10f)
                    .rotate(-11f),
            )
        }
        if (canAddStickers && guideClear(3) && stickers.none { it.slot == 3 }) {
            ProfileBoardStickerSlot(
                onClick = { onAction(ProfileBoardAction.OpenStickerPicker(3)) },
                shape = CircleShape,
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(end = boardWidth * 0.01f, bottom = boardWidth * (BoardStickerPlacement.HEIGHT - layout.height))
                    .size(boardWidth * 0.24f)
                    .rotate(12f),
            )
        }
        ProfileBoardStickerLayer(
            stickers = visibleStickers,
            hiddenSlot = draggedStickerSlot,
            layout = layout,
            isEditing = isEditing,
            canStartEditing = canAddStickers,
            focusedStickerSlot = focusedStickerSlot,
            onAction = onAction,
            onDraggingChanged = onDraggingChanged,
            modifier = Modifier.matchParentSize(),
        )
    }
}

/** Loading covers both server rendering and the image download, including the save transition. */
internal fun shouldShowQrLoading(
    styleId: String,
    render: BoardQrRender?,
    isEditing: Boolean,
    isSaving: Boolean,
    imageShown: Boolean,
    imageFailed: Boolean,
): Boolean {
    if (BoardQrTheme.find(styleId) == null || (isEditing && !isSaving) || imageFailed || imageShown) return false
    val matching = render?.takeIf { it.themeId == styleId }
    if (matching?.status == BoardQrRenderStatus.FALLBACK || matching?.status == BoardQrRenderStatus.NONE) return false
    // A malformed READY response has no image to wait for; retain the regular QR.
    if (matching?.status == BoardQrRenderStatus.READY && matching.imageUrl.isNullOrBlank()) return false
    return true
}
