package com.kaanf.home.presentation.eventcode.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import androidx.compose.ui.unit.sp

enum class CodeFieldStatus { Editing, Success, Error }

@Composable
fun CodeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 4,
    status: CodeFieldStatus = CodeFieldStatus.Editing,
    enabled: Boolean = true,
    cellSpacing: Dp = 12.dp,
) {
    val editable = enabled && status != CodeFieldStatus.Success
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = { raw ->
            val sanitized = raw.filter { it.isLetterOrDigit() }
                .take(length)
                .uppercase()
            if (sanitized != value) onValueChange(sanitized)
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        enabled = editable,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        cursorBrush = SolidColor(Color.Transparent),
        decorationBox = {
            val cursorIndex = if (value.length < length) value.length else length - 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (enabled) 1f else DISABLED_ALPHA),
                horizontalArrangement = Arrangement.spacedBy(cellSpacing),
            ) {
                repeat(length) { index ->
                    CodeCell(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        char = value.getOrNull(index),
                        showCursor = enabled &&
                            isFocused &&
                            status == CodeFieldStatus.Editing &&
                            index == cursorIndex,
                        status = status,
                    )
                }
            }
        },
    )
}

@Composable
private fun CodeCell(
    char: Char?,
    showCursor: Boolean,
    status: CodeFieldStatus,
    modifier: Modifier = Modifier,
) {
    val filled = char != null

    val borderTarget = when {
        status == CodeFieldStatus.Success -> Accent
        status == CodeFieldStatus.Error -> ErrorRed
        filled || showCursor -> Accent
        else -> EmptyBorder
    }
    val fillTarget = when (status) {
        CodeFieldStatus.Success -> SuccessFill
        CodeFieldStatus.Error -> EmptyFill
        CodeFieldStatus.Editing -> EmptyFill
    }

    val borderColor by animateColorAsState(borderTarget, tween(250), label = "border")
    val fillColor by animateColorAsState(fillTarget, tween(250), label = "fill")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(fillColor)
            .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (char != null) {
                BasicText(
                    text = char.toString(),
                    style = TextStyle(
                        color = Cream,
                        fontSize = 32.sp,
                        fontFamily = BricolageGrotesque,
                        fontWeight = FontWeight.ExtraBold,
                        fontFeatureSettings = "tnum",
                        textAlign = TextAlign.Center,
                    ),
                )
            }
            if (showCursor) {
                BlinkingCursor()
            }
        }
    }
}

@Composable
private fun BlinkingCursor(
    color: Color = Accent,
    height: Dp = 32.dp,
) {
    val transition = rememberInfiniteTransition(label = "cursor")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "cursorAlpha",
    )
    Box(
        modifier = Modifier
            .width(2.dp)
            .height(height)
            .graphicsLayer { this.alpha = alpha }
            .background(color, RoundedCornerShape(1.dp)),
    )
}

private const val DISABLED_ALPHA = 0.7f

private val Accent = AccessDefaults.Accent
private val Cream = AccessDefaults.TextPrimary
private val EmptyFill = AccessDefaults.SurfaceElevated
private val EmptyBorder = AccessDefaults.Border
private val SuccessFill = AccessDefaults.Accent.copy(alpha = 0.12f)
private val ErrorRed = AccessDefaults.Error
