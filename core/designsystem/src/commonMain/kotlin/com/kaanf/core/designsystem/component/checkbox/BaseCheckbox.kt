package com.kaanf.core.designsystem.component.checkbox

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BaseCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = AccessShapes.XSmall
    val indicatorColor = if (checked) AccessDefaults.Accent else AccessDefaults.SurfaceElevated
    val indicatorBorderColor = if (checked) AccessDefaults.Accent else AccessDefaults.Border
    val checkColor = AccessDefaults.Background
    val textColor = if (enabled) AccessDefaults.TextPrimary else AccessDefaults.TextFaint

    Row(
        modifier = modifier.clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            onClick = { onCheckedChange(!checked) },
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(indicatorColor, shape)
                .border(width = 1.5.dp, color = indicatorBorderColor, shape = shape)
                .then(
                    if (testTag != null) {
                        Modifier.testTag(testTag)
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Canvas(
                    modifier = Modifier
                        .size(14.dp)
                        .padding(1.dp),
                ) {
                    val path = Path().apply {
                        moveTo(size.width * 0.18f, size.height * 0.52f)
                        lineTo(size.width * 0.42f, size.height * 0.76f)
                        lineTo(size.width * 0.84f, size.height * 0.26f)
                    }
                    drawPath(
                        path = path,
                        color = checkColor,
                        style = Stroke(
                            width = 2.4.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )
                }
            }
        }

        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 12.sp
            ),
        )
    }
}

@Preview
@Composable
private fun BaseCheckboxPreview() {
    CrewTheme(isDarkTheme = true) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            BaseCheckbox(
                checked = false,
                onCheckedChange = {},
                label = "Unchecked",
            )
            BaseCheckbox(
                checked = true,
                onCheckedChange = {},
                label = "Checked",
            )
        }
    }
}
