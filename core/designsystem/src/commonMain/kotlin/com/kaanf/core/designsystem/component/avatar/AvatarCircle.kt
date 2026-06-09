package com.kaanf.core.designsystem.component.avatar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import androidx.compose.ui.tooling.preview.Preview

/**
 * What an [AvatarCircle] renders. The two states are mutually exclusive: an avatar
 * either shows a profile [Image] or falls back to colored [Initials] — never both.
 */
sealed interface AvatarContent {
    data class Image(val url: String) : AvatarContent
    data class Initials(
        val label: String,
        val color: Color,
        val textColor: Color = Color(0xFF17110D),
    ) : AvatarContent
}

@Composable
fun AvatarCircle(
    content: AvatarContent,
    avatarSize: Int = 46,
    textSize: Double = (avatarSize / 2.5),
    borderColor: Color = AccessDefaults.AvatarBorder,
    borderSize: Int = (avatarSize / 14),
    modifier: Modifier = Modifier,
) {
    val innerColor = when (content) {
        is AvatarContent.Initials -> content.color
        is AvatarContent.Image -> AccessDefaults.SurfaceElevated
    }
    Box(
        modifier = modifier
            .size(avatarSize.dp)
            .drawBehind {
                val borderWidth = borderSize.dp.toPx()
                val radius = size.minDimension / 2f
                drawCircle(
                    color = borderColor,
                    radius = radius,
                )
                drawCircle(
                    color = innerColor,
                    radius = radius - borderWidth,
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        when (content) {
            is AvatarContent.Image -> AsyncImage(
                model = content.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(borderSize.dp)
                    .fillMaxSize()
                    .clip(CircleShape),
            )

            is AvatarContent.Initials -> Text(
                text = content.label,
                color = content.textColor,
                fontSize = textSize.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
@Preview
fun AvatarCirclePreview() {
    AvatarCircle(AvatarContent.Initials(label = "M", color = Color(0xFFC8FF3D)))
}
