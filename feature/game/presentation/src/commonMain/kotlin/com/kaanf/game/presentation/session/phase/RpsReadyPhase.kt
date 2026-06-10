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
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.avatar.AvatarContent
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.game.presentation.component.EmojiStackCard
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_rps_ready_action
import crew.feature.game.presentation.generated.resources.match_phase_rps_ready_description
import crew.feature.game.presentation.generated.resources.match_phase_rps_ready_loading
import crew.feature.game.presentation.generated.resources.match_phase_rps_ready_title
import crew.feature.game.presentation.generated.resources.match_phase_rps_ready_vs_label
import crew.feature.game.presentation.generated.resources.match_unknown_avatar_label
import crew.feature.game.presentation.generated.resources.match_you_avatar_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RpsReadyPhase(
    opponentFullName: String,
    isWaiting: Boolean,
    onReadyClick: () -> Unit,
    modifier: Modifier = Modifier,
    opponentImageUrl: String? = null,
    myImageUrl: String? = null,
) {
    val unknownAvatarLabel = stringResource(Res.string.match_unknown_avatar_label)
    val opponentInitial = opponentFullName.take(1).uppercase().ifBlank { unknownAvatarLabel }

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = 24.dp,
                alignment = Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarCircle(
                content = myImageUrl?.let { AvatarContent.Image(it) }
                    ?: AvatarContent.Initials(
                        label = stringResource(Res.string.match_you_avatar_label),
                        color = AccessDefaults.Rose,
                    ),
                avatarSize = 78,
                textSize = 30.0,
                borderColor = AccessDefaults.BorderSoft,
                borderSize = 2,
            )

            Text(
                text = stringResource(Res.string.match_phase_rps_ready_vs_label),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextMuted,
                    letterSpacing = 3.sp,
                    fontSize = 12.sp,
                ),
            )

            AvatarCircle(
                content = avatarContentFor(
                    imageUrl = opponentImageUrl,
                    initialsLabel = opponentInitial,
                    seed = opponentFullName,
                ),
                avatarSize = 78,
                textSize = 30.0,
                borderColor = AccessDefaults.BorderSoft,
                borderSize = 2,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.match_phase_rps_ready_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(Res.string.match_phase_rps_ready_description),
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        EmojiStackCard(
            size = 84.dp,
            isWaving = false,
        )

        Spacer(modifier = Modifier.height(12.dp))

        BaseButton(
            text = stringResource(Res.string.match_phase_rps_ready_action),
            onClick = onReadyClick,
            filled = true,
            isLoading = isWaiting,
            loadingText = stringResource(Res.string.match_phase_rps_ready_loading, opponentInitial),
        )
    }
}

@Composable
@Preview
fun RpsReadyPhasePreview() {
    CrewTheme {
        RpsReadyPhase(
            opponentFullName = "Kaan",
            isWaiting = false,
            onReadyClick = {},
        )
    }
}
