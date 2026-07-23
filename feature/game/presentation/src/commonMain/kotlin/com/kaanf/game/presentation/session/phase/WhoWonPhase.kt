package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.avatarPaletteColor
import com.kaanf.core.designsystem.component.progressbar.ThreeDotsAnimatedCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.presentation.model.GameResultOptionUi
import com.kaanf.game.presentation.model.WhoWonAvatarUi
import com.kaanf.game.presentation.session.component.WhoWonRow
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_who_won_description
import crew.feature.game.presentation.generated.resources.match_phase_who_won_eyebrow
import crew.feature.game.presentation.generated.resources.match_phase_who_won_lost_description
import crew.feature.game.presentation.generated.resources.match_phase_who_won_lost_emoji
import crew.feature.game.presentation.generated.resources.match_phase_who_won_lost_points
import crew.feature.game.presentation.generated.resources.match_phase_who_won_lost_title
import crew.feature.game.presentation.generated.resources.match_phase_who_won_title
import crew.feature.game.presentation.generated.resources.match_phase_who_won_waiting
import crew.feature.game.presentation.generated.resources.match_phase_who_won_won_description
import crew.feature.game.presentation.generated.resources.match_phase_who_won_won_emoji
import crew.feature.game.presentation.generated.resources.match_phase_who_won_won_title
import crew.feature.game.presentation.generated.resources.match_points_format
import crew.feature.game.presentation.generated.resources.match_unknown_avatar_label
import crew.feature.game.presentation.generated.resources.match_you_avatar_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WhoWonPhase(
    opponentFullName: String,
    isReporting: Boolean,
    onResult: (won: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    myClaimWon: Boolean? = null,
    opponentClaimedMeWon: Boolean? = null,
    opponentImageUrl: String? = null,
    myImageUrl: String? = null,
) {
    val unknownAvatarLabel = stringResource(Res.string.match_unknown_avatar_label)
    val opponentInitial = opponentFullName.take(1).uppercase().ifBlank { unknownAvatarLabel }
    val opponentName = opponentFullName.ifBlank { opponentInitial }
    val meAvatar = WhoWonAvatarUi(
        label = stringResource(Res.string.match_you_avatar_label),
        color = AccessDefaults.Rose,
        imageUrl = myImageUrl,
    )
    val opponentAvatar = WhoWonAvatarUi(
        label = opponentInitial,
        color = avatarPaletteColor(opponentFullName),
        imageUrl = opponentImageUrl,
        highlight = true,
    )

    val wonRowAvatars = buildList {
        if (myClaimWon == true) add(meAvatar)
        if (opponentClaimedMeWon == true) add(opponentAvatar)
    }
    val lostRowAvatars = buildList {
        if (myClaimWon == false) add(meAvatar)
        if (opponentClaimedMeWon == false) add(opponentAvatar)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.match_phase_who_won_eyebrow),
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp
            ),
        )

        Text(
            text = stringResource(Res.string.match_phase_who_won_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(Res.string.match_phase_who_won_description, opponentInitial),
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        WhoWonRow(
            optionUi = GameResultOptionUi(
                title = stringResource(Res.string.match_phase_who_won_won_title),
                // Backend'deki WIN_BONUS_POINTS ile aynı: kazanan her maçta sabit 5 bonus alır.
                pointText = stringResource(Res.string.match_points_format, 5),
                description = stringResource(
                    Res.string.match_phase_who_won_won_description,
                    opponentName,
                ),
                emoji = stringResource(Res.string.match_phase_who_won_won_emoji),
            ),
            enabled = !isReporting,
            avatars = wonRowAvatars,
            onClick = { onResult(true) },
        )

        WhoWonRow(
            optionUi = GameResultOptionUi(
                title = stringResource(Res.string.match_phase_who_won_lost_title),
                pointText = stringResource(Res.string.match_phase_who_won_lost_points),
                description = stringResource(
                    Res.string.match_phase_who_won_lost_description,
                    opponentName,
                ),
                emoji = stringResource(Res.string.match_phase_who_won_lost_emoji),
            ),
            enabled = !isReporting,
            avatars = lostRowAvatars,
            onClick = { onResult(false) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isReporting) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 6.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThreeDotsAnimatedCard(
                    dotRadius = 2.dp,
                    spacing = 4.dp
                )
                Text(
                    text = stringResource(Res.string.match_phase_who_won_waiting, opponentInitial),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextSecondary,
                        fontSize = 11.sp,
                    ),
                )
            }
        }
        // Dispute butonu kaldırıldı: onClick boştu, geçen etkinlikte kafa karıştırdı.
        // Host'a itiraz akışı gelince geri ekle (string kaynağı duruyor).
    }
}


@Composable
@Preview
fun WhoWonPhasePreview() {
    CrewTheme {
        WhoWonPhase(
            opponentFullName = "Kaan",
            isReporting = false,
            onResult = {},
        )
    }
}
