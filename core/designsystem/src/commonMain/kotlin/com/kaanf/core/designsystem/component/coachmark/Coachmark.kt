package com.kaanf.core.designsystem.component.coachmark

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min
import kotlin.math.roundToInt

@Immutable
data class CoachmarkStep(
    val key: Any? = null,
    val title: String,
    val body: String,
    val highlight: String? = null,
    val cornerRadius: Dp = 16.dp,
    val padding: Dp = 12.dp,
)

@Stable
class CoachmarkState internal constructor(
    internal val steps: List<CoachmarkStep>,
) {
    internal val bounds = mutableStateMapOf<Any, Rect>()
    internal var hostOrigin by mutableStateOf(Offset.Zero)
    internal var index by mutableIntStateOf(0)
}

val LocalCoachmarkState = compositionLocalOf<CoachmarkState?> { null }

@Composable
fun Modifier.coachmarkTarget(key: Any): Modifier {
    val state = LocalCoachmarkState.current ?: return this
    return onGloballyPositioned { coordinates ->
        val rect = coordinates.boundsInRoot()
        if (state.bounds[key] != rect) state.bounds[key] = rect
    }
}

@Composable
fun CoachmarkHost(
    steps: List<CoachmarkStep>,
    visible: Boolean,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = remember(steps) { CoachmarkState(steps) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                val origin = coordinates.boundsInRoot().topLeft
                if (state.hostOrigin != origin) state.hostOrigin = origin
            },
    ) {
        CompositionLocalProvider(LocalCoachmarkState provides state) {
            content()
        }
        if (visible && steps.isNotEmpty()) {
            CoachmarkOverlay(state = state, onFinish = onFinish)
        }
    }
}

@Composable
private fun CoachmarkOverlay(
    state: CoachmarkState,
    onFinish: () -> Unit,
) {
    val step = state.steps[state.index]
    val isLast = state.index == state.steps.lastIndex
    val next: () -> Unit = { if (isLast) onFinish() else state.index++ }

    val hole = step.key
        ?.let { state.bounds[it] }
        ?.translate(-state.hostOrigin)
        ?.inflate(with(LocalDensity.current) { step.padding.toPx() })

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { } }
            .drawBehind {
                if (hole == null) {
                    drawRect(ScrimColor)
                } else {
                    val path = Path().apply {
                        fillType = PathFillType.EvenOdd
                        addRect(Rect(Offset.Zero, size))
                        addRoundRect(RoundRect(hole, CornerRadius(step.cornerRadius.toPx())))
                    }
                    drawPath(path, ScrimColor)
                }
            },
    ) {
        val density = LocalDensity.current
        val marginPx = with(density) { EdgeMargin.toPx() }
        val bubbleWidthPx = min(
            with(density) { BubbleMaxWidth.toPx() },
            constraints.maxWidth - 2 * marginPx,
        )

        if (hole == null) {
            CoachmarkBubble(
                step = step,
                stepIndex = state.index,
                stepCount = state.steps.size,
                isLast = isLast,
                arrow = null,
                arrowX = 0f,
                onSkip = onFinish,
                onBack = { state.index-- },
                onNext = next,
                modifier = Modifier
                    .align(Alignment.Center)
                    .layout { measurable, _ ->
                        val placeable = measurable.measure(
                            Constraints(maxWidth = bubbleWidthPx.roundToInt()),
                        )
                        layout(placeable.width, placeable.height) { placeable.place(0, 0) }
                    },
            )
        } else {
            val gapPx = with(density) { BubbleGap.toPx() }
            val arrowInsetPx = with(density) { ArrowEdgeInset.toPx() }
            val placeBelow =
                constraints.maxHeight - hole.bottom > with(density) { BelowSpaceThreshold.toPx() }
            val left = (hole.center.x - bubbleWidthPx / 2)
                .coerceIn(marginPx, constraints.maxWidth - marginPx - bubbleWidthPx)
            val arrowX = (hole.center.x - left)
                .coerceIn(arrowInsetPx, bubbleWidthPx - arrowInsetPx)

            CoachmarkBubble(
                step = step,
                stepIndex = state.index,
                stepCount = state.steps.size,
                isLast = isLast,
                arrow = if (placeBelow) CoachmarkArrow.Top else CoachmarkArrow.Bottom,
                arrowX = arrowX,
                onSkip = onFinish,
                onBack = { state.index-- },
                onNext = next,
                modifier = Modifier.layout { measurable, layoutConstraints ->
                    val placeable = measurable.measure(
                        Constraints(maxWidth = bubbleWidthPx.roundToInt()),
                    )
                    val y = if (placeBelow) hole.bottom + gapPx else hole.top - gapPx - placeable.height
                    val minY = marginPx.roundToInt()
                    val maxY = (layoutConstraints.maxHeight - placeable.height - marginPx)
                        .roundToInt()
                        .coerceAtLeast(minY)
                    layout(layoutConstraints.maxWidth, layoutConstraints.maxHeight) {
                        placeable.place(
                            x = left.roundToInt(),
                            y = y.roundToInt().coerceIn(minY, maxY),
                        )
                    }
                },
            )
        }
    }
}

internal enum class CoachmarkArrow { Top, Bottom }

private val ScrimColor = Color(0xD9080604)
private val BubbleMaxWidth = 302.dp
private val EdgeMargin = 14.dp
private val BubbleGap = 14.dp
private val ArrowEdgeInset = 20.dp
private val BelowSpaceThreshold = 220.dp
