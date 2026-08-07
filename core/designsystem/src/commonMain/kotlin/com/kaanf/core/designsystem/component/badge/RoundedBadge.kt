package com.kaanf.core.designsystem.component.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RoundedBadge(
    modifier: Modifier = Modifier,
    backgroundColor: Color = AccessDefaults.SurfaceElevated,
    borderColor: Color = AccessDefaults.BorderSoft,
    textColor: Color = AccessDefaults.TextSecondary,
    isLive: Boolean = false,
    /** null = labelMedium'un kendi kalınlığı. */
    fontWeight: FontWeight? = null,
    text: String
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .background(
                color = backgroundColor,
                shape = AccessShapes.Medium
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = AccessShapes.Medium
            )
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            )
    ) {
        val labelStyle = MaterialTheme.typography.labelMedium
        Text(
            text = text,
            style = labelStyle.copy(
                fontSize = 11.sp,
                color = textColor,
                fontWeight = fontWeight ?: labelStyle.fontWeight,
            )
        )
    }
}

@Preview
@Composable
fun RoundedBadgePreview() {
    CrewTheme {
        RoundedBadge(text = "%42 full")
    }
}
