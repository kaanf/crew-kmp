package com.kaanf.core.designsystem.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun BaseInputField(
    modifier: Modifier = Modifier,
    isFocused: Boolean = false,
    enabled: Boolean = true,
    minHeight: Dp = 52.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    val backgroundColor =
        if (isFocused) {
            AccessDefaults.SurfaceHigh
        } else {
            AccessDefaults.SurfaceElevated
        }
    val borderColor =
        if (isFocused) {
            AccessDefaults.Accent
        } else {
            AccessDefaults.Border
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .alpha(if (enabled) 1f else 0.5f)
                .background(AccessDefaults.AccentGlow, shape)
                .border(
                    width = if (isFocused) 1.dp else 0.dp,
                    color = if (isFocused) AccessDefaults.AccentGlow else backgroundColor,
                    shape = shape,
                )
                .background(backgroundColor, shape)
                .border(1.dp, borderColor, shape),
        content = content,
    )
}
