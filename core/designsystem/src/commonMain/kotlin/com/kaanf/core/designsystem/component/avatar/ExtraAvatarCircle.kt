package com.kaanf.core.designsystem.component.avatar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ExtraAvatarCircle(
    count: Int,
    avatarSize: Int = 46,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(avatarSize.dp)
            .drawBehind {
                val borderWidth = (avatarSize/14).dp.toPx()
                val radius = size.minDimension / 2f
                drawCircle(
                    color = Color(0xFF14100C),
                    radius = radius
                )
                drawCircle(
                    color = Color(0xFF312B23),
                    radius = radius - borderWidth
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$count",
            color = Color(0xFFD3C9BA),
            fontSize = (avatarSize/2.6).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview
fun ExtraAvatarCirclePreview() {
    ExtraAvatarCircle(count = 3)
}
