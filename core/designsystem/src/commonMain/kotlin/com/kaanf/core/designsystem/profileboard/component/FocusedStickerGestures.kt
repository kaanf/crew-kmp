package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.kaanf.core.presentation.util.haptics.ImpactHaptics
import com.kaanf.core.presentation.util.haptics.rememberImpactHaptics
import com.kaanf.core.designsystem.profileboard.ProfileBoardAction
import com.kaanf.core.domain.model.profileboard.BoardLayout
import com.kaanf.core.domain.model.profileboard.BoardSticker
import com.kaanf.core.domain.model.profileboard.BoardStickerPlacement
import kotlin.math.abs

/** Common ancestor receives both fingers even when they land outside the small sticker. */
@Composable
internal fun Modifier.focusedStickerGestures(
    enabled: Boolean,
    stickers: List<BoardSticker>,
    focusedStickerSlot: Int?,
    layout: BoardLayout,
    widthPx: Float,
    onAction: (ProfileBoardAction) -> Unit,
    onTransforming: (Boolean) -> Unit,
    onDragPosition: (Int, Offset) -> Unit = { _, _ -> },
    onDrop: (Int) -> Boolean = { false },
    onDragFinished: () -> Unit = {},
): Modifier {
    val latestStickers = rememberUpdatedState(stickers)
    val latestFocus = rememberUpdatedState(focusedStickerSlot)
    val latestLayout = rememberUpdatedState(layout)
    val latestAction = rememberUpdatedState(onAction)
    val latestTransforming = rememberUpdatedState(onTransforming)
    val latestDragPosition = rememberUpdatedState(onDragPosition)
    val latestDrop = rememberUpdatedState(onDrop)
    val latestDragFinished = rememberUpdatedState(onDragFinished)
    val impact = rememberImpactHaptics()
    if (!enabled || widthPx <= 0f) return this
    return pointerInput(widthPx, enabled) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            fun boundsOf(sticker: BoardSticker): Rect {
                val (width, height) = BoardStickerPlacement.footprint(sticker, latestLayout.value)
                val centre = Offset(sticker.position.x * widthPx, sticker.position.y * widthPx)
                return Rect(centre.x - width * widthPx / 2f, centre.y - height * widthPx / 2f,
                    centre.x + width * widthPx / 2f, centre.y + height * widthPx / 2f)
            }
            val ordered = latestStickers.value.sortedBy { if (it.slot == latestFocus.value) 1 else 0 }
            val hit = ordered.lastOrNull { boundsOf(it).inflate(12.dp.toPx()).contains(down.position) }
            val target = hit ?: return@awaitEachGesture
            val bounds = boundsOf(target)
            val handle = Rect(bounds.left, bounds.bottom - 24.dp.toPx(), bounds.left + 24.dp.toPx(), bounds.bottom)
            // The explicit corner handle owns its one-finger gesture.
            val remove = Rect(bounds.right - 24.dp.toPx(), bounds.top, bounds.right, bounds.top + 24.dp.toPx())
            if ((target.slot == latestFocus.value && handle.contains(down.position)) || remove.contains(down.position)) return@awaitEachGesture
            val grabOffset = bounds.center - down.position
            latestAction.value(ProfileBoardAction.FocusSticker(target.slot))
            var active = false
            var totalPan = Offset.Zero
            var released = false
            val feedback = StickerGestureFeedback(impact, target, down.uptimeMillis)
            try {
                do {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val cancelled = event.changes.any { it.isConsumed }
                    if (!cancelled) {
                        val pan = event.calculatePan()
                        val zoom = event.calculateZoom()
                        val rotation = event.calculateRotation()
                        val multiTouch = event.changes.count { it.pressed && it.previousPressed } >= 2
                        totalPan += pan
                        val wasActive = active
                        if (!active) {
                            active = (multiTouch && (zoom != 1f || rotation != 0f || pan != Offset.Zero)) ||
                                (totalPan.getDistance() > viewConfiguration.touchSlop)
                            if (active) {
                                latestTransforming.value(true)
                                impact.perform()
                            }
                        }
                        if (active) {
                            val delta = if (!wasActive && !multiTouch) totalPan else pan
                            latestAction.value(ProfileBoardAction.TransformSticker(
                                target.slot, delta.x / widthPx, delta.y / widthPx, zoom, rotation, isGesture = true,
                            ))
                            latestStickers.value.find { it.slot == target.slot }?.let { feedback.update(it, event.changes.first().uptimeMillis) }
                            latestDragPosition.value(target.slot, event.changes.first().position + grabOffset)
                            event.changes.forEach { it.consume() }
                        }
                    }
                    released = !cancelled && event.changes.none { it.pressed }
                } while (!cancelled && event.changes.any { it.pressed })
            } finally {
                if (active) {
                    val deleted = released && latestDrop.value(target.slot)
                    if (!deleted) latestAction.value(ProfileBoardAction.FinishStickerGesture(target.slot))
                    if (released) impact.perform()
                    latestDragFinished.value()
                    latestTransforming.value(false)
                }
            }
        }
    }
}

internal class StickerGestureFeedback(
    private val impact: ImpactHaptics,
    private var previous: BoardSticker,
    private var lastTime: Long,
) {
    fun update(sticker: BoardSticker, time: Long) {
        val moved = Offset(sticker.position.x - previous.position.x, sticker.position.y - previous.position.y)
            .getDistance() >= 0.02f
        val transformed = abs(sticker.scale - previous.scale) >= 0.06f || abs(sticker.rotation - previous.rotation) >= 10f
        if ((moved || transformed) && time - lastTime >= 110L) {
            impact.perform()
            previous = sticker
            lastTime = time
        }
    }
}
