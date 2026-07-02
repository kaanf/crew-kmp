package com.kaanf.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    textColor: Color? = null,
) {
    val borderWidth = 1.5.dp
    val outerShape = RoundedCornerShape(10.dp)
    val innerShape = RoundedCornerShape(10.dp - borderWidth)
    val interactionSource = remember { MutableInteractionSource() }

    val resolvedBorderColor =
        borderColor ?: when {
            danger -> AccessDefaults.DangerBorder
            filled -> AccessDefaults.Accent
            else -> AccessDefaults.Border
        }
    val resolvedBackgroundColor =
        backgroundColor ?: when {
            danger -> AccessDefaults.DangerBackground
            filled -> AccessDefaults.Accent
            else -> AccessDefaults.Surface
        }
    val resolvedContentColor =
        textColor ?: when {
            danger && (enabled || isLoading) -> AccessDefaults.LeftArrowColor
            filled && (enabled || isLoading) -> AccessDefaults.OnAccent
            enabled || isLoading -> AccessDefaults.TextPrimary
            else -> AccessDefaults.TextFaint
        }

    Box(
        modifier = modifier
            .height(36.dp)
            // Loading shares the dimmed "disabled" look: normal background at 0.5 alpha.
            .alpha(if (enabled && !isLoading) 1f else 0.5f)
            .clip(outerShape)
            .background(resolvedBorderColor)
            .padding(borderWidth)
            .clip(innerShape)
            .background(resolvedBackgroundColor)
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .defaultMinSize(minWidth = 64.dp)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        // The label always defines the button width; while loading it stays laid out but
        // invisible so the spinner overlay doesn't shrink the button.
        Row(
            modifier = Modifier.alpha(if (isLoading) 0f else 1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    tint = resolvedContentColor,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = resolvedContentColor,
                ),
            )
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = resolvedContentColor,
                strokeWidth = 2.dp,
            )
        }
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
