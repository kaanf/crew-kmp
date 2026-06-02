package com.kaanf.game.presentation.component

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.JetbrainsMono
import com.kaanf.core.presentation.model.ChallengeCardUiModel
import com.kaanf.core.presentation.model.ChallengeCardVariant

@Composable
fun WinnerPickChallengeCard(
    modifier: Modifier = Modifier,
    card: ChallengeCardUiModel,
    isExpanded: Boolean = false
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AccessDefaults.Surface,
                shape = AccessShapes.Large,
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.Border,
                shape = AccessShapes.Large,
            )
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
                text = "+${card.points} PTS",
                color = card.variant.getColor(),
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

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PointCard(
                    text = "ACCEPT",
                    point = 35,
                    color = AccessDefaults.Accent,
                    isLeft = true
                )

                PointCard(
                    text = "REJECT",
                    point = 5,
                    color = AccessDefaults.LeftArrowColor,
                    isLeft = false
                )
            }
        }
    }
}

@Composable
private fun PointCard(
    text: String,
    point: Int,
    color: Color,
    isLeft: Boolean
) {
    Column(
        modifier = Modifier
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = if (isLeft) Alignment.Start else Alignment.End
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 10.sp,
            ),
        )

        Text(
            text = "+$point PTS",
            color = color,
            fontSize = 12.sp,
            fontFamily = JetbrainsMono,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun CardBadge(
    variant: ChallengeCardVariant,
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(
                color = variant.getColor().copy(alpha = 0.1f),
                shape = AccessShapes.XSmall,
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = variant.name.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = variant.getColor(),
            ),
        )
    }
}

private fun ChallengeCardVariant.getColor(): Color {
    return when (this) {
        ChallengeCardVariant.Social -> AccessDefaults.Sky
        ChallengeCardVariant.Bold -> AccessDefaults.Coral
        ChallengeCardVariant.Icebreaker -> AccessDefaults.Accent
        ChallengeCardVariant.Flirty -> AccessDefaults.Rose
        ChallengeCardVariant.Team -> AccessDefaults.Teal
        ChallengeCardVariant.Funny -> AccessDefaults.Amber
    }
}
