package com.kaanf.game.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.AvatarContent
import com.kaanf.core.designsystem.component.badge.RoundedBadge
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun HostInfoCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AvatarCircle(
            content = AvatarContent.Initials(label = "KF", color = AccessDefaults.Rose),
            avatarSize = 48,
            textSize = 16.0,
            borderColor = AccessDefaults.BorderSoft,
            borderSize = 2,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = AccessDefaults.TextPrimary,
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append(
                            "Kaan F.",
                        )
                    }

                    withStyle(
                        style = SpanStyle(
                            color = AccessDefaults.TextMuted,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        ),
                    ) {
                        append(
                            "\tyour host"
                        )
                    }
                },
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontSize = 15.sp
                )
            )

            Text(
                text = "Find the black shirt to say hi!",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        RoundedBadge(
            text = "Host",
            backgroundColor = AccessDefaults.SurfaceElevated,
            borderColor = Color.Transparent,
            textColor = AccessDefaults.TextSecondary,
        )
    }
}
