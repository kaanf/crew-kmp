package com.kaanf.core.designsystem.profileboard.component

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.profileboard.ProfileBoardAction
import com.kaanf.core.domain.model.profileboard.*
import com.kaanf.core.presentation.util.haptics.rememberImpactHaptics
import kotlin.math.abs

/** Events use the fixed canvas coordinates even while the QR view moves underneath the finger. */
@Composable
internal fun Modifier.qrBoardGestures(
    enabled: Boolean,
    qr: BoardQrPlacement,
    stickers: List<BoardSticker>,
    widthPx: Float,
    onAction: (ProfileBoardAction) -> Unit,
    onDragging: (Boolean) -> Unit,
): Modifier {
    val latestQr = rememberUpdatedState(qr)
    val latestStickers = rememberUpdatedState(stickers)
    val latestAction = rememberUpdatedState(onAction)
    val latestDragging = rememberUpdatedState(onDragging)
    val impact = rememberImpactHaptics()
    if (!enabled || widthPx <= 0f) return this
    return pointerInput(enabled, widthPx) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            val start = latestQr.value
            val bounds = start.bounds()
            val qrRect = Rect(bounds.left * widthPx, bounds.top * widthPx, bounds.right * widthPx, bounds.bottom * widthPx)
            if (!qrRect.contains(down.position)) return@awaitEachGesture
            val touchesSticker = latestStickers.value.any { sticker ->
                val (w, h) = BoardStickerPlacement.footprint(sticker, BoardLayout(qr = bounds))
                Rect((sticker.position.x - w / 2) * widthPx, (sticker.position.y - h / 2) * widthPx,
                    (sticker.position.x + w / 2) * widthPx, (sticker.position.y + h / 2) * widthPx)
                    .inflate(12.dp.toPx()).contains(down.position)
            }
            if (touchesSticker) return@awaitEachGesture
            latestAction.value(ProfileBoardAction.FocusQr)
            var active = false
            var totalPan = Offset.Zero
            var totalZoom = 1f
            var feedbackQr = start
            var lastFeedbackTime = down.uptimeMillis
            try {
                do {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val cancelled = event.changes.any { it.isConsumed }
                    if (!cancelled) {
                        val pan = event.calculatePan()
                        val zoom = event.calculateZoom()
                        totalPan += pan
                        totalZoom *= zoom
                        val wasActive = active
                        if (!active && (totalPan.getDistance() > viewConfiguration.touchSlop ||
                                abs(1f - totalZoom) * start.size * widthPx > viewConfiguration.touchSlop)) {
                            active = true
                            latestDragging.value(true)
                            impact.perform()
                        }
                        if (active) {
                            val delta = if (wasActive) pan else totalPan
                            latestAction.value(ProfileBoardAction.TransformQr(delta.x / widthPx, delta.y / widthPx,
                                if (wasActive) zoom else totalZoom, isGesture = true))
                            val current = latestQr.value
                            val now = event.changes.first().uptimeMillis
                            val moved = Offset(current.x - feedbackQr.x, current.y - feedbackQr.y).getDistance() >= .02f
                            if ((moved || abs(current.size - feedbackQr.size) >= .025f) && now - lastFeedbackTime >= 110L) {
                                impact.perform()
                                feedbackQr = current
                                lastFeedbackTime = now
                            }
                            event.changes.forEach { it.consume() }
                        }
                    }
                } while (!cancelled && event.changes.any { it.pressed })
            } finally {
                if (active) {
                    latestAction.value(ProfileBoardAction.FinishQrGesture)
                    impact.perform()
                    latestDragging.value(false)
                }
            }
        }
    }
}
