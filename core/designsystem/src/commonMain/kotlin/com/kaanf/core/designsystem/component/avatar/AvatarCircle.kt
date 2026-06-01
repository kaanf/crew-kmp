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
import com.kaanf.core.designsystem.theme.AccessDefaults
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AvatarCircle(
    label: String,
    color: Color,
    textColor: Color = Color(0xFF17110D),
    avatarSize: Int = 46,
    textSize: Double = (avatarSize/2.5),
    borderColor: Color = AccessDefaults.AvatarBorder,
    borderSize: Int = (avatarSize/14),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(avatarSize.dp)
            .drawBehind {
                val borderWidth = borderSize.dp.toPx()
                val radius = size.minDimension / 2f
                drawCircle(
                    color = borderColor,
                    radius = radius
                )
                drawCircle(
                    color = color,
                    radius = radius - borderWidth
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = textSize.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
@Preview
fun AvatarCirclePreview() {
    AvatarCircle(label = "M", color = Color(0xFFC8FF3D))
}
