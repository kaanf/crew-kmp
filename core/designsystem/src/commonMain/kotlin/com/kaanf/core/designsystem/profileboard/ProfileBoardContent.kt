package com.kaanf.core.designsystem.profileboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.util.haptics.rememberImpactHaptics
import kotlin.math.roundToInt
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import com.kaanf.core.domain.model.profileboard.BoardQrPlacement
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.kaanf.core.designsystem.component.background.DottedBackground

import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.kaanf.core.designsystem.profileboard.component.*
import com.kaanf.core.domain.model.profileboard.BoardStickerPlacement
import crew.core.designsystem.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** One board renderer for both screens; only the owner is given editing callbacks. */
@Composable
fun ProfileBoardContent(
    state: ProfileBoardState,
    modifier: Modifier = Modifier,
    onAction: (ProfileBoardAction) -> Unit = {},
    onRetry: () -> Unit = {},
    onClose: (() -> Unit)? = null,
    onEditProfile: (() -> Unit)? = null,
    qrModifier: Modifier = Modifier,
    footerReservedHeight: Dp = 0.dp,
    footer: @Composable () -> Unit = {},
    onQrImageError: () -> Unit = {},
) {
    val owner = onClose != null && onEditProfile != null
    val editing = owner && state.isEditingBoard && !state.isSaving && !state.hasConflict && !state.hasLoadError && state.canEdit
    var dragging by remember { mutableStateOf(false) }
    var draggedSlot by remember { mutableStateOf<Int?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var dragScale by remember { mutableStateOf(1f) }
    var rootOrigin by remember { mutableStateOf(Offset.Zero) }
    var trashBounds by remember { mutableStateOf(Rect.Zero) }
    val overTrash = draggedSlot != null && trashBounds.contains(dragPosition)
    val impact = rememberImpactHaptics()
    LaunchedEffect(overTrash) { if (overTrash) impact.perform() }
    LaunchedEffect(editing) { if (!editing) { dragging = false; draggedSlot = null } }
    val density = LocalDensity.current
    var identityHeight by remember { mutableStateOf(160.dp) }
    var panelHeight by remember { mutableStateOf(180.dp) }
    val scrollState = rememberScrollState()
    BoxWithConstraints(modifier.fillMaxSize().clipToBounds()
        .onGloballyPositioned { rootOrigin = it.localToRoot(Offset.Zero) }) {
        val panelOpen = editing && state.isEditorSheetOpen
        val transition = updateTransition(panelOpen, label = "board-editor")
        val progress = transition.animateFloat(
            transitionSpec = { tween(if (targetState) 300 else 220, easing = FastOutSlowInEasing) },
            label = "editor-progress",
        ) { if (it) 1f else 0f }
        val dragPanelOffset by animateFloatAsState(if (draggedSlot != null) 1f else 0f, tween(180), label = "drag-panel")
        val panelPresent = panelOpen || transition.currentState || transition.isRunning
        LaunchedEffect(panelOpen) { if (panelOpen) scrollState.scrollTo(0) }
        val availableBoardHeight = (maxHeight - (if (owner) 44.dp else 8.dp)
            - footerReservedHeight - 24.dp).coerceAtLeast(0.dp)
        val referenceWidth = minOf(maxWidth - 40.dp,
            (availableBoardHeight / BoardStickerPlacement.HEIGHT).coerceAtMost(420.dp)).coerceAtLeast(1.dp)
        // Reserve the entire profile band, including both sides and the 15 dp gap above it.
        val placementBottom = (BoardStickerPlacement.HEIGHT -
            ((identityHeight + 15.dp - footerReservedHeight).coerceAtLeast(0.dp) / referenceWidth))
            .coerceIn(BoardQrPlacement.MAX_SIZE, BoardStickerPlacement.HEIGHT)
        val layout = state.boardLayout.copy(contentTop = 0f, height = placementBottom)
        LaunchedEffect(layout) {
            if (layout != state.boardLayout) onAction(ProfileBoardAction.LayoutChanged(layout))
        }
        val dottedWidth = maxWidth
        val canvas: @Composable () -> Unit = {
            if (state.isLoading) ProfileBoardLoadingCanvas(Modifier.width(referenceWidth))
            else ProfileBoardCanvas(
                modifier = Modifier.width(referenceWidth),
                dottedWidth = dottedWidth,
                dottedHeight = availableBoardHeight,
                publicCode = state.publicCode,
                qrStyleId = state.qrStyleId,
                qrRender = state.qrRender,
                qr = state.qr,
                boardLayout = layout,
                isQrFocused = state.isQrFocused,
                stickers = state.stickers,
                isEditing = editing,
                isSaving = state.isSaving,
                onQrImageError = onQrImageError,
                canAddStickers = owner && state.canEdit && !state.isLoading && !state.isSaving && !state.hasLoadError && !state.hasConflict,
                focusedStickerSlot = state.focusedStickerSlot,
                onAction = onAction,
                onDraggingChanged = { dragging = it },
                draggedStickerSlot = draggedSlot,
                onStickerDrag = { slot, position, scale ->
                    draggedSlot = slot
                    dragPosition = position
                    dragScale = scale
                },
                onStickerDrop = { slot ->
                    if (trashBounds.contains(dragPosition)) {
                        onAction(ProfileBoardAction.DeleteSticker(slot))
                        true
                    } else false
                },
                onStickerDragFinished = { draggedSlot = null },
                qrModifier = qrModifier,
            )
        }
        DottedBackground(Modifier.matchParentSize())
        Column(Modifier.fillMaxSize()) {
            // Keep the scrolling/animated sticker viewport below the overlaid controls.
            // Clipping this area also protects the controls during dragging and editor motion.
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()
                .padding(top = if (owner) 44.dp else 8.dp).clipToBounds()) {
                val editorHeight = (maxHeight - panelHeight).coerceAtLeast(0.dp)
                val editorScale = minOf(1f, (maxWidth - 32.dp) / referenceWidth,
                    (editorHeight - 32.dp) / (referenceWidth * placementBottom)).coerceAtLeast(0f)
                val editorTop = (editorHeight - referenceWidth * placementBottom * editorScale) / 2
                Column(
                    modifier = Modifier.fillMaxSize()
                        .verticalScroll(scrollState, enabled = !dragging && !panelPresent)
                        .padding(horizontal = 20.dp).padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // One persistent canvas: animation reads happen only in layer/draw phases.
                    BoardViewport(referenceWidth, 1f,
                        modifier = Modifier.animatedBoardViewport(progress, editorScale, editorTop - 24.dp,
                            placementBottom / BoardStickerPlacement.HEIGHT),
                        content = canvas)
                    if (!panelPresent) {
                        if (state.hasLoadError) {
                            TextButton(onClick = onRetry) { Text(stringResource(Res.string.profile_board_retry)) }
                        }
                        if (state.hasConflict) {
                            TextButton(onClick = { onAction(ProfileBoardAction.ReloadBoard) }) {
                                Text(stringResource(Res.string.profile_board_reload))
                            }
                        }
                        footer()
                    }
                }
            }
        }
        if (owner) ProfileBoardControls(
            modifier = Modifier.align(Alignment.TopCenter).padding(horizontal = 20.dp),
            onClose = onClose,
            onEditBoard = { onAction(if (state.isEditingBoard) ProfileBoardAction.FinishEditing else ProfileBoardAction.EditBoard) },
            isEditing = state.isEditingBoard,
            isSaving = state.isSaving,
            onSettings = onEditProfile,
            canEdit = state.canEdit && !state.isLoading && !state.hasLoadError && !state.hasConflict,
            onCancel = { onAction(ProfileBoardAction.CancelEditing) },
        )

        val identityModifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            .graphicsLayer { alpha = if (draggedSlot != null) 0f else 1f - progress.value }
            .then(if (panelPresent || draggedSlot != null) Modifier.clearAndSetSemantics {} else Modifier)
            .onSizeChanged { identityHeight = with(density) { it.height.toDp() } }
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
        if (state.isLoading && state.fullName.isBlank()) ProfileBoardIdentityLoading(identityModifier)
        else ProfileBoardIdentity(
            fullName = state.fullName,
            profilePictureUrl = state.profilePictureUrl,
            modifier = identityModifier,
        )

        if (panelPresent) {
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .onSizeChanged { panelHeight = with(density) { it.height.toDp() } }
                .graphicsLayer { translationY = size.height * maxOf(1f - progress.value, dragPanelOffset) }
                .then(if (draggedSlot != null) Modifier.clearAndSetSemantics {} else Modifier)) {
                BoardEditorPanel(state, onAction = { if (panelOpen) onAction(it) }, isActive = panelOpen)
            }
        }
        if (draggedSlot != null) {
            val removeDescription = stringResource(Res.string.profile_sticker_remove)
            Box(
                Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp)
                    .size(88.dp)
                    .onGloballyPositioned { trashBounds = it.boundsInRoot() }
                    .background(if (overTrash) AccessDefaults.Accent else AccessDefaults.SurfaceElevated, CircleShape)
                    .border(2.dp, if (overTrash) AccessDefaults.TextPrimary else AccessDefaults.BorderSoft, CircleShape)
                    .semantics { contentDescription = removeDescription },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Delete, null, tint = AccessDefaults.TextPrimary, modifier = Modifier.size(32.dp))
            }
            state.stickers.find { it.slot == draggedSlot }?.let { sticker ->
                val width = referenceWidth * (sticker.design.width * layout.stickerScale * sticker.scale * dragScale)
                val height = referenceWidth * (sticker.design.height * layout.stickerScale * sticker.scale * dragScale)
                BoardStickerArtwork(sticker.design, Modifier
                    .absoluteOffset {
                        IntOffset(
                            (dragPosition.x - rootOrigin.x - with(density) { width.toPx() } / 2).roundToInt(),
                            (dragPosition.y - rootOrigin.y - with(density) { height.toPx() } / 2).roundToInt(),
                        )
                    }.size(width, height).rotate(sticker.rotation).clearAndSetSemantics {})
            }
        }

    }
}
