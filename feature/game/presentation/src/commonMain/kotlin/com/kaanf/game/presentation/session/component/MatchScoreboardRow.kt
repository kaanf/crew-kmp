package com.kaanf.game.presentation.session.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.AvatarContent
import com.kaanf.core.designsystem.modifier.surfaceCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.domain.model.MatchScoreboardEntry
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_points_format
import crew.feature.game.presentation.generated.resources.match_scoreboard_detail_completed_task_subtitle
import crew.feature.game.presentation.generated.resources.match_scoreboard_detail_completed_task_title
import crew.feature.game.presentation.generated.resources.match_scoreboard_detail_lost_throw_title
import crew.feature.game.presentation.generated.resources.match_scoreboard_detail_rps_subtitle
import crew.feature.game.presentation.generated.resources.match_scoreboard_detail_task_not_done_subtitle
import crew.feature.game.presentation.generated.resources.match_scoreboard_detail_task_not_done_title
import crew.feature.game.presentation.generated.resources.match_scoreboard_detail_won_throw_title
import crew.feature.game.presentation.generated.resources.match_scoreboard_lost_throw_label
import crew.feature.game.presentation.generated.resources.match_scoreboard_won_throw_label
import crew.feature.game.presentation.generated.resources.match_scoreboard_you_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Puan tablosundaki tek oyuncunun kartı: isim/puan satırı + role'e göre açıklama alt-satırı.
 * [isYou] çağıran oyuncuyu işaretler (isim "You" olur, avatar vurgulanır); [taskCompleted]
 * yalnızca kaybedenin alt-satırını sürer (kazananın bonusu her hâlükârda yazılır).
 */
@Composable
fun MatchScoreboardCard(
    entry: MatchScoreboardEntry,
    isYou: Boolean,
    taskCompleted: Boolean,
    forfeit: Boolean = false,
    photoUrl: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .surfaceCard(
                backgroundColor = if (isYou) {
                    AccessDefaults.Accent.copy(0.1f)
                } else {
                    AccessDefaults.Surface
                },
                borderColor = if (isYou) AccessDefaults.Accent else AccessDefaults.Border,
            )
            .padding(
                all = 16.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MatchScoreboardRow(entry = entry, isYou = isYou, photoUrl = photoUrl)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    color = AccessDefaults.Border,
                ),
        )

        ScoreboardDetailRow(entry = entry, taskCompleted = taskCompleted, forfeit = forfeit)
    }
}

@Composable
private fun ScoreboardDetailRow(
    entry: MatchScoreboardEntry,
    taskCompleted: Boolean,
    forfeit: Boolean,
) {
    if (entry.isWinner) {
        DetailRow(
            icon = Icons.Rounded.Check,
            tint = AccessDefaults.Accent,
            title = stringResource(Res.string.match_scoreboard_detail_won_throw_title),
            subtitle = stringResource(Res.string.match_scoreboard_detail_rps_subtitle),
        )
        return
    }

    // Kaybeden için önce atışı kaybettiğini gösteren işaretsiz satır, ardından görev satırı.
    DetailRow(
        icon = Icons.Rounded.Close,
        tint = AccessDefaults.LeftArrowColor,
        title = stringResource(Res.string.match_scoreboard_detail_lost_throw_title),
        subtitle = stringResource(Res.string.match_scoreboard_detail_rps_subtitle),
    )

    // Forfeit'te görev hiç oynanmadı; "görev yapılmadı" satırını gösterme.
    if (forfeit) return

    if (taskCompleted) {
        DetailRow(
            icon = Icons.Rounded.Check,
            tint = AccessDefaults.Accent,
            title = stringResource(Res.string.match_scoreboard_detail_completed_task_title),
            subtitle = stringResource(Res.string.match_scoreboard_detail_completed_task_subtitle),
        )
    } else {
        DetailRow(
            icon = Icons.Rounded.Close,
            tint = AccessDefaults.LeftArrowColor,
            title = stringResource(Res.string.match_scoreboard_detail_task_not_done_title),
            subtitle = stringResource(Res.string.match_scoreboard_detail_task_not_done_subtitle),
        )
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    tint: Color,
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AccessShapes.Large)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(AccessShapes.XSmall)
                .background(tint.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontSize = 13.sp,
                ),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.sp,
                ),
            )
        }
    }
}

@Composable
fun MatchScoreboardRow(
    entry: MatchScoreboardEntry,
    isYou: Boolean,
    photoUrl: String? = null,
) {
    val displayName = if (isYou) stringResource(Res.string.match_scoreboard_you_label) else entry.fullName
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AvatarCircle(
                content = if (!photoUrl.isNullOrBlank()) {
                    AvatarContent.Image(photoUrl)
                } else {
                    AvatarContent.Initials(
                        label = displayName.firstOrNull()?.uppercase().orEmpty(),
                        color = if (entry.isWinner) AccessDefaults.Accent else AccessDefaults.Sky,
                    )
                },
                avatarSize = 56,
                borderSize = 2,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = AccessDefaults.TextPrimary,
                        fontSize = 20.sp,
                    ),
                )

                Text(
                    text = if (entry.isWinner) {
                        stringResource(Res.string.match_scoreboard_won_throw_label)
                    } else {
                        stringResource(Res.string.match_scoreboard_lost_throw_label)
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextMuted,
                        fontSize = 11.sp,
                    ),
                )
            }
        }

        Text(
            text = stringResource(Res.string.match_points_format, entry.points),
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (entry.points > 0) AccessDefaults.Accent else AccessDefaults.TextMuted,
                fontSize = 16.sp,
            ),
        )
    }
}

@Composable
@Preview
fun MatchScoreboardRowPreview() {
    CrewTheme {
        MatchScoreboardCard(
            entry = MatchScoreboardEntry(
                participantId = "p1",
                userId = "u1",
                fullName = "Kaan",
                isWinner = true,
                points = 3,
            ),
            isYou = true,
            taskCompleted = true,
        )
    }
}
