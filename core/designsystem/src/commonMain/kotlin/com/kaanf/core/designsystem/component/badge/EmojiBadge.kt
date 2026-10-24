package com.kaanf.core.designsystem.component.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Görsellerin köşesine yapışan, hafif eğik tek emojilik rozet (ör. biletli etkinlikte 🎟️). */
@Composable
fun EmojiBadge(
    emoji: String,
    modifier: Modifier = Modifier,
    borderColor: Color = AccessDefaults.Accent,
) {
    Box(
        modifier = modifier
            .rotate(-10f)
            .size(40.dp)
            .background(color = Color.Black.copy(alpha = 0.55f), shape = CircleShape)
            .border(width = 1.5.dp, color = borderColor, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, fontSize = 20.sp)
    }
}

@Preview
@Composable
private fun EmojiBadgePreview() {
    CrewTheme {
        EmojiBadge(emoji = "🎟️")
    }
}
