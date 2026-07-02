package com.kaanf.core.presentation.util

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.clearFocusOnTap(): Modifier {
    val focusManager = LocalFocusManager.current
    // Compose's FocusManager only knows about Compose focus. The auth fields are native interop
    // views (UITextField / EditText) whose focus + keyboard live outside Compose, so we also have
    // to resign the native first responder, otherwise tapping outside leaves the keyboard up.
    val clearNativeFocus = rememberNativeFocusClearer()
    return this.pointerInput(Unit) {
        detectTapGestures {
            focusManager.clearFocus()
            clearNativeFocus()
        }
    }
}

fun Modifier.dottedBorder(
    color: Color,
    shape: RoundedCornerShape,
    strokeWidth: Dp = 1.dp,
    dotLength: Dp = 2.dp,
    gapLength: Dp = 6.dp,
): Modifier = this.drawBehind {
    val strokePx = strokeWidth.toPx()
    val halfStroke = strokePx / 2

    val outline = shape.createOutline(
        size = size,
        layoutDirection = layoutDirection,
        density = this
    )

    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(dotLength.toPx(), gapLength.toPx()),
        phase = 0f
    )

    when (outline) {
        is Outline.Rounded -> {
            val radius = outline.roundRect.topLeftCornerRadius.x

            drawRoundRect(
                color = color,
                topLeft = Offset(halfStroke, halfStroke),
                size = Size(
                    width = size.width - strokePx,
                    height = size.height - strokePx
                ),
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(
                    width = strokePx,
                    pathEffect = pathEffect,
                    cap = StrokeCap.Round
                )
            )
        }

        is Outline.Rectangle -> {
            drawRect(
                color = color,
                topLeft = Offset(halfStroke, halfStroke),
                size = Size(
                    width = size.width - strokePx,
                    height = size.height - strokePx
                ),
                style = Stroke(
                    width = strokePx,
                    pathEffect = pathEffect,
                    cap = StrokeCap.Round
                )
            )
        }

        else -> Unit
    }
}
