package com.kaanf.game.presentation.session.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameHomeTopBar(
    userName: String,
    photoUrl: String?,
    score: Int,
    winCount: Int,
    matchesCount: Int,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val losses = (matchesCount - winCount).coerceAtLeast(0)
    val winRate = if (matchesCount == 0) 0 else (winCount * 100) / matchesCount

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AccessDefaults.Background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarCircle(
                content = avatarContentFor(
                    imageUrl = photoUrl,
                    initialsLabel = userName.take(1).uppercase(),
                    seed = userName,
                ),
                avatarSize = 52,
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = userName.substringBefore(" "),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = AccessDefaults.TextPrimary,
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )

                    Text(
                        text = "$score Pts",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.Accent,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }

                WinRateBar(
                    winRate = winRate,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = "$winCount WIN · $losses LOSS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextMuted,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            Spacer(Modifier.width(12.dp))

            IconButton(
                onClick = onCloseClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AccessDefaults.SurfaceElevated)
                    .border(width = 1.dp, color = AccessDefaults.BorderSoft, shape = CircleShape)
                    .size(32.dp),
            ) {
                Icon(
                    painter = painterResource(AccessIcons.Close),
                    contentDescription = null,
                    tint = AccessDefaults.TextPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

/** Kazanma oranı şeridi: [winRate] (0-100) kadarı accent dolu, kalanı sönük. */
@Composable
private fun WinRateBar(
    winRate: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(AccessDefaults.SurfaceElevated),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(winRate.coerceIn(0, 100) / 100f)
                .height(6.dp)
                .background(AccessDefaults.Accent),
        )
    }
}

@Composable
@Preview
private fun GameHomeTopBarPreview() {
    CrewTheme {
        GameHomeTopBar(
            userName = "Kaan Fındık",
            photoUrl = null,
            score = 1240,
            winCount = 6,
            matchesCount = 9,
            onCloseClick = {},
        )
    }
}
