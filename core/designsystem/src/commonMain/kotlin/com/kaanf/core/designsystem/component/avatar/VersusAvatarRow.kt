package com.kaanf.core.designsystem.component.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * İki oyuncuyu karşı karşıya koyan avatar satırı. Ortadaki [center] slotu duruma göre
 * değişir: "VS" etiketi, davet gönderildi çizgisi, eşleşme halkası…
 */
@Composable
fun VersusAvatarRow(
    left: AvatarContent,
    right: AvatarContent,
    modifier: Modifier = Modifier,
    avatarSize: Int = 78,
    textSize: Double = 30.0,
    leftBorderColor: Color = AccessDefaults.BorderSoft,
    rightBorderColor: Color = AccessDefaults.BorderSoft,
    center: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = 24.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarCircle(
            content = left,
            avatarSize = avatarSize,
            textSize = textSize,
            borderColor = leftBorderColor,
            borderSize = 2,
        )

        center()

        AvatarCircle(
            content = right,
            avatarSize = avatarSize,
            textSize = textSize,
            borderColor = rightBorderColor,
            borderSize = 2,
        )
    }
}

@Preview
@Composable
private fun VersusAvatarRowPreview() {
    CrewTheme {
        VersusAvatarRow(
            left = AvatarContent.Initials(label = "You", color = AccessDefaults.Rose),
            right = AvatarContent.Initials(label = "M", color = AccessDefaults.Teal),
        ) {
            Text(
                text = "VS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextMuted,
                    letterSpacing = 3.sp,
                    fontSize = 12.sp,
                ),
            )
        }
    }
}
