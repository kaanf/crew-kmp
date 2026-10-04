package com.kaanf.game.presentation.session.phase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.avatar.AvatarContent
import com.kaanf.core.designsystem.component.avatar.VersusAvatarRow
import com.kaanf.core.designsystem.component.avatar.avatarContentFor
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_rps_ready_action
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

    // Başlık ekranın tam ortasında: üst ve alt eşit ağırlıkta; avatarlar üst boşluğun,
    // buton alt boşluğun içinde durur.
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            VersusAvatarRow(
                left = myImageUrl?.let { AvatarContent.Image(it) }
                    ?: AvatarContent.Initials(
                        label = stringResource(Res.string.match_you_avatar_label),
                        color = AccessDefaults.Rose,
                    ),
                right = avatarContentFor(
                    imageUrl = opponentImageUrl,
                    initialsLabel = opponentInitial,
                    seed = opponentFullName,
                ),
                avatarSize = 56,
                textSize = 20.0,
            ) {
                Text(
                    text = stringResource(Res.string.match_phase_rps_ready_vs_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextMuted,
                        letterSpacing = 3.sp,
                        fontSize = 12.sp,
                    ),
                )
            }
        }

        Text(
            text = stringResource(Res.string.match_phase_rps_ready_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            BaseButton(
                text = stringResource(Res.string.match_phase_rps_ready_action),
                onClick = onReadyClick,
                filled = true,
                isLoading = isWaiting,
                loadingText = stringResource(
                    Res.string.match_phase_rps_ready_loading,
                    opponentFullName.ifBlank { opponentInitial },
                ),
            )
        }
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
