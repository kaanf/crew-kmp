package com.kaanf.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BaseMiniButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    loadingText: String = text,
    filled: Boolean = false,
    danger: Boolean = false,
    leadingIcon: DrawableResource? = null,
) {
    val borderWidth = 1.5.dp
    val outerShape = RoundedCornerShape(10.dp)
    val innerShape = RoundedCornerShape(10.dp - borderWidth)
    val interactionSource = remember { MutableInteractionSource() }

    val borderColor =
        when {
            danger -> AccessDefaults.DangerBorder
            filled -> AccessDefaults.Accent
            else -> AccessDefaults.Border
        }
    val backgroundColor =
        when {
            isLoading -> AccessDefaults.FieldFocusedBackground
            danger -> AccessDefaults.DangerBackground
            filled -> AccessDefaults.Accent
            else -> AccessDefaults.Surface
        }
    val contentColor =
        when {
            isLoading -> AccessDefaults.LoadingButtonText
            danger && enabled -> AccessDefaults.LeftArrowColor
            filled && enabled -> AccessDefaults.OnAccent
            enabled -> AccessDefaults.TextPrimary
            else -> AccessDefaults.TextFaint
        }

    Row(
        modifier = modifier
            .height(36.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(outerShape)
            .background(borderColor)
            .padding(borderWidth)
            .clip(innerShape)
            .background(backgroundColor)
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .defaultMinSize(minWidth = 64.dp)
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null && !isLoading) {
            Icon(
                painter = painterResource(leadingIcon),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = if (isLoading) loadingText else text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
            ),
        )
    }
}

@Preview
@Composable
private fun BaseMiniButtonPreview() {
    CrewTheme {
        Column(
            modifier = Modifier
                .background(Color(0xFF0E0B08))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BaseMiniButton(
                    text = "Join",
                    onClick = {},
                    filled = true,
                )
                BaseMiniButton(
                    text = "Details",
                    onClick = {},
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BaseMiniButton(
                    text = "Disabled",
                    onClick = {},
                    enabled = false,
                )
                BaseMiniButton(
                    text = "Loading",
                    onClick = {},
                    isLoading = true,
                    loadingText = "...",
                    filled = true,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BaseMiniButton(
                    text = "Etkinlikten çık",
                    onClick = {},
                    danger = true,
                )
            }
        }
    }
}
