package com.kaanf.game.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.badge.RoundedBadge
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.JetbrainsMono
import com.kaanf.core.presentation.model.ChallengeCardUiModel
import com.kaanf.core.presentation.model.ChallengeCardVariant
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_points_format
import crew.feature.game.presentation.generated.resources.match_points_signed_format
import crew.feature.game.presentation.generated.resources.match_task_card_reject_label
import crew.feature.game.presentation.generated.resources.match_task_card_variant_bold
import crew.feature.game.presentation.generated.resources.match_task_card_variant_challenge
import crew.feature.game.presentation.generated.resources.match_task_card_variant_confession
import crew.feature.game.presentation.generated.resources.match_task_card_variant_flirty
import crew.feature.game.presentation.generated.resources.match_task_card_variant_icebreaker
import crew.feature.game.presentation.generated.resources.match_task_card_variant_photo
import crew.feature.game.presentation.generated.resources.match_task_card_variant_storytime
import crew.feature.game.presentation.generated.resources.match_task_card_variant_team
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameTaskCard(
    modifier: Modifier = Modifier,
    card: ChallengeCardUiModel,
    isExpanded: Boolean = false,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AccessDefaults.Surface,
                shape = AccessShapes.Large,
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) card.variant.taskAccentColor() else AccessDefaults.Border,
                shape = AccessShapes.Large,
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CardBadge(card.variant)

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.match_points_format, card.points),
                color = card.variant.taskAccentColor(),
                fontSize = 12.sp,
                fontFamily = JetbrainsMono,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        Text(
            text = card.description,
            color = Color(0xFFF4EEE8),
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )

        if (isExpanded) {
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .height(1.dp)
                    .background(AccessDefaults.BorderSoft),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Üst satırın (rozet solda / puan sağda) aynası: reject cezası tek satır.
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.match_task_card_reject_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccessDefaults.TextMuted,
                        fontSize = 10.sp,
                    ),
                )

                Text(
                    // Ceza işaretli gelir (örn. -35), o yüzden +'sız formatla basılır.
                    text = stringResource(Res.string.match_points_signed_format, card.rejectPoints),
                    color = AccessDefaults.LeftArrowColor,
                    fontSize = 12.sp,
                    fontFamily = JetbrainsMono,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
private fun CardBadge(
    variant: ChallengeCardVariant,
) {
    val label = when (variant) {
        ChallengeCardVariant.Icebreaker -> stringResource(Res.string.match_task_card_variant_icebreaker)
        ChallengeCardVariant.Team -> stringResource(Res.string.match_task_card_variant_team)
        ChallengeCardVariant.Storytime -> stringResource(Res.string.match_task_card_variant_storytime)
        ChallengeCardVariant.Challenge -> stringResource(Res.string.match_task_card_variant_challenge)
        ChallengeCardVariant.Photo -> stringResource(Res.string.match_task_card_variant_photo)
        ChallengeCardVariant.Bold -> stringResource(Res.string.match_task_card_variant_bold)
        ChallengeCardVariant.Confession -> stringResource(Res.string.match_task_card_variant_confession)
        ChallengeCardVariant.Flirty -> stringResource(Res.string.match_task_card_variant_flirty)
    }
    val accent = variant.taskAccentColor()

    RoundedBadge(
        text = "${variant.emoji} $label",
        backgroundColor = accent.copy(alpha = 0.12f),
        borderColor = accent.copy(alpha = 0.55f),
        textColor = accent,
        fontWeight = FontWeight.Bold,
    )
}

internal fun ChallengeCardVariant.taskAccentColor(): Color {
    return when (this) {
        ChallengeCardVariant.Icebreaker -> AccessDefaults.Accent
        ChallengeCardVariant.Team -> AccessDefaults.Teal
        ChallengeCardVariant.Storytime -> AccessDefaults.Amber
        ChallengeCardVariant.Challenge -> AccessDefaults.Sky
        ChallengeCardVariant.Photo -> AccessDefaults.Mint
        ChallengeCardVariant.Bold -> AccessDefaults.Coral
        ChallengeCardVariant.Confession -> AccessDefaults.Violet
        ChallengeCardVariant.Flirty -> AccessDefaults.Rose
    }
}
