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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RoundedBadge(
    modifier: Modifier = Modifier,
    title: String
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .background(
                color = AccessDefaults.SurfaceElevated,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.BorderSoft,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                color = AccessDefaults.TextSecondary
            )
        )
    }
}

@Preview
@Composable
fun RoundedBadgePreview() {
    CrewTheme {
        RoundedBadge(title = "%42 full")
    }
}
