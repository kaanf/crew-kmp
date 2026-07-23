package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.dottedBorder
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.TaskCategory
import com.kaanf.game.presentation.component.GameTaskCard
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_onboarding_info_text
import crew.feature.game.presentation.generated.resources.match_phase_loser_active_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoserActiveTaskPhase(
    opponentName: String,
    task: GameTask?,
    modifier: Modifier = Modifier,
    opponentImageUrl: String? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        task?.let {
            GameTaskCard(
                card = it.toUiModel(),
                isExpanded = true,
            )
        }

        WinnerIsWatchingCard(
            opponentName = opponentName,
            opponentImageUrl = opponentImageUrl,
        )
    }
}

@Composable
private fun WinnerIsWatchingCard(
    opponentName: String,
    opponentImageUrl: String? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dottedBorder(
                color = AccessDefaults.Border,
                shape = AccessShapes.Medium,
                strokeWidth = 1.dp,
                dotLength = 2.dp,
                gapLength = 4.dp,
            ),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AvatarCircle(
                    content = avatarContentFor(
                        imageUrl = opponentImageUrl,
                        initialsLabel = opponentName.take(1).uppercase().ifBlank { "?" },
                        seed = opponentName,
                    ),
                    avatarSize = 48,
                    borderSize = 2
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.match_phase_loser_active_title, opponentName),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.TextPrimary,
                            fontSize = 13.sp
                        ),
                    )

                    Text(
                        text = "They will confirm when you complete the task.",
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.TextMuted,
                            fontSize = 12.sp
                        ),
                    )
                }
            }
        },
    )
}

@Composable
@Preview
private fun Preview() {
    CrewTheme {
        LoserActiveTaskPhase(
            opponentName = "Mira",
            task = GameTask(
                id = "1",
                title = "🌍 Get two strangers to teach you the same word in their language.",
                points = 20,
                categories = listOf(TaskCategory.Social),
            ),
        )
    }
}

