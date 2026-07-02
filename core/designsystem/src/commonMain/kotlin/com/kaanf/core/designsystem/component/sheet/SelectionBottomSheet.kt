package com.kaanf.core.designsystem.component.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun <T> SelectionBottomSheet(
    title: String,
    options: List<T>,
    selected: T?,
    labelOf: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    ContainerBottomSheet(
        onDismiss = onDismiss,
        dismissible = true,
        showDragHandle = true,
        content = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = AccessDefaults.TextPrimary,
                        textAlign = TextAlign.Start,
                    ),
                )

                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AccessDefaults.TextSecondary,
                            fontSize = 13.sp,
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                options.forEach { option ->
                    SelectionRow(
                        label = labelOf(option),
                        isSelected = option == selected,
                        onClick = { onSelect(option) },
                    )
                }
            }
        },
    )
}

@Composable
private fun SelectionRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                )
                .background(
                    if (isSelected) AccessDefaults.AccentFocusBg else AccessDefaults.SurfaceElevated,
                    shape,
                )
                .border(
                    width = 1.dp,
                    color = if (isSelected) AccessDefaults.Accent else AccessDefaults.Border,
                    shape = shape,
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(color = AccessDefaults.TextPrimary),
            modifier = Modifier.weight(1f),
        )

        RadioIndicator(isSelected = isSelected)
    }
}

@Composable
private fun RadioIndicator(isSelected: Boolean) {
    Box(
        modifier =
            Modifier
                .size(20.dp)
                .border(
                    width = 2.dp,
                    color = if (isSelected) AccessDefaults.Accent else AccessDefaults.TextFaint,
                    shape = CircleShape,
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Box(
                modifier =
                    Modifier
                        .size(10.dp)
                        .background(AccessDefaults.Accent, CircleShape),
            )
        }
    }
}
