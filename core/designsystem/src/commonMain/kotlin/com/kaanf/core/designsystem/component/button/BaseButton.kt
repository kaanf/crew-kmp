package com.kaanf.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BaseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    loadingText: String = "AUTHENTICATING...",
    filled: Boolean = false,
) {
    val shape = RoundedCornerShape(14.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val borderColor =
        when {
            filled -> AccessDefaults.Accent
            else -> AccessDefaults.Border
        }

    val backgroundColor =
        when {
            isLoading -> AccessDefaults.FieldFocusedBackground
            filled -> AccessDefaults.Accent
            else -> AccessDefaults.Surface
        }

    val contentColor =
        when {
            isLoading -> AccessDefaults.LoadingButtonText
            filled && enabled -> AccessDefaults.OnAccent
            enabled -> AccessDefaults.TextPrimary
            else -> AccessDefaults.TextFaint
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(52.dp)
                .alpha(if (enabled) 1f else 0.5f)
                .background(backgroundColor, shape)
                .border(1.dp, borderColor, shape)
                .clickable(
                    enabled = enabled && !isLoading,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isLoading) loadingText else text,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = contentColor,
            ),
        )
    }
}

@Preview
@Composable
private fun BaseButtonPreview() {
    CrewTheme(isDarkTheme = true) {
        Column(
            modifier =
                Modifier
                    .background(Color(0xFF0E0B08))
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BaseButton(
                text = "Primary",
                onClick = {},
                filled = true,
            )
            BaseButton(
                text = "Secondary",
                onClick = {},
            )
            BaseButton(
                text = "Disabled secondary",
                onClick = {},
                enabled = false,
            )
            BaseButton(
                text = "Disabled primary",
                onClick = {},
                enabled = false,
                filled = true,
            )
            BaseButton(
                text = "Loading",
                onClick = {},
                isLoading = true,
                loadingText = "Loading...",
                filled = true,
            )
        }
    }
}
