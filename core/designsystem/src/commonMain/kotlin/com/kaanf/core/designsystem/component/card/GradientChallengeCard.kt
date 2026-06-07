package com.kaanf.core.designsystem.component.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.JetbrainsMono
import com.kaanf.core.presentation.model.ChallengeCardUiModel
import com.kaanf.core.presentation.model.ChallengeCardVariant
import com.kaanf.core.presentation.util.dottedBorder

@Composable
fun GradientChallengeCard(
    modifier: Modifier = Modifier,
    card: ChallengeCardUiModel,
) {
    val colors = getChallengeCardColor(card.variant)

    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "challenge_card_press_scale"
    )

    Box(
        modifier = modifier
            .width(200.dp)
            .height(200.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    colors = colors.backgroundColors,
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY,
                ),
            )
            .drawWithCache {
                val corner = 28.dp.toPx()

                val glowBrush = Brush.radialGradient(
                    colors = colors.glowColors,
                    center = Offset(0f, 0f),
                    radius = size.width * 1.45f,
                )

                onDrawBehind {
                    drawRoundRect(
                        brush = glowBrush,
                        cornerRadius = CornerRadius(corner, corner),
                    )
                }
            }
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(colors.borderColors),
                shape = RoundedCornerShape(28.dp),
            )
            .padding(16.dp),
        content = {
            Column(
                modifier = Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "● ${card.variant.name.uppercase()}",
                    color = card.variant.getColor(),
                    fontFamily = JetbrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                )

                Text(
                    text = card.description,
                    color = Color(0xFFF4EEE8),
                    fontSize = 14.sp,
                    minLines = 4,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )

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
        },
    )
}

@Immutable
private data class ChallengeCardColors(
    val backgroundColors: List<Color>,
    val glowColors: List<Color>,
    val borderColors: List<Color>,
    val accent: Color,
    val textPrimary: Color = Color(0xFFF4EEE8),
    val textSecondary: Color = Color(0xFF6E625B),
)

private fun getChallengeCardColor(variant: ChallengeCardVariant): ChallengeCardColors {
    return when (variant) {

        ChallengeCardVariant.Social -> ChallengeCardColors(
            backgroundColors = listOf(
                Color(0xFF101923),
                Color(0xFF0E151D),
                Color(0xFF0B1118),
                Color(0xFF090D12),
                Color(0xFF07090D),
            ),
            glowColors = listOf(
                Color(0x336FB7FF),
                Color(0x226FB7FF),
                Color.Transparent,
            ),
            borderColors = listOf(
                Color(0xFF29435E),
                Color(0xFF172838),
            ),
            accent = AccessDefaults.Sky,
        )

        ChallengeCardVariant.Bold -> ChallengeCardColors(
            backgroundColors = listOf(
                Color(0xFF211511),
                Color(0xFF1C130F),
                Color(0xFF17100D),
                Color(0xFF120D0A),
                Color(0xFF0C0B08),
            ),
            glowColors = listOf(
                Color(0x33FF7A5C),
                Color(0x22FF7A5C),
                Color.Transparent,
            ),
            borderColors = listOf(
                Color(0xFF5A2F25),
                Color(0xFF2E1914),
            ),
            accent = AccessDefaults.Coral,
        )

        ChallengeCardVariant.Icebreaker -> ChallengeCardColors(
            backgroundColors = listOf(
                Color(0xFF1B2110),
                Color(0xFF171C0E),
                Color(0xFF12170B),
                Color(0xFF0E1208),
                Color(0xFF090B06),
            ),
            glowColors = listOf(
                Color(0x33C8FF3D),
                Color(0x22C8FF3D),
                Color.Transparent,
            ),
            borderColors = listOf(
                Color(0xFF4B6120),
                Color(0xFF293511),
            ),
            accent = AccessDefaults.Accent,
        )

        ChallengeCardVariant.Flirty -> ChallengeCardColors(
            backgroundColors = listOf(
                Color(0xFF241018),
                Color(0xFF1E0E14),
                Color(0xFF180B10),
                Color(0xFF12080C),
                Color(0xFF0B0608),
            ),
            glowColors = listOf(
                Color(0x33FF5A7A),
                Color(0x22FF5A7A),
                Color.Transparent,
            ),
            borderColors = listOf(
                Color(0xFF5E2433),
                Color(0xFF35141D),
            ),
            accent = AccessDefaults.Rose,
        )

        ChallengeCardVariant.Team -> ChallengeCardColors(
            backgroundColors = listOf(
                Color(0xFF10211D),
                Color(0xFF0E1B18),
                Color(0xFF0B1513),
                Color(0xFF08100E),
                Color(0xFF050B0A),
            ),
            glowColors = listOf(
                Color(0x335BE0C5),
                Color(0x225BE0C5),
                Color.Transparent,
            ),
            borderColors = listOf(
                Color(0xFF24594F),
                Color(0xFF14332D),
            ),
            accent = AccessDefaults.Teal,
        )

        ChallengeCardVariant.Funny -> ChallengeCardColors(
            backgroundColors = listOf(
                Color(0xFF241A0D),
                Color(0xFF1E150B),
                Color(0xFF171008),
                Color(0xFF120C06),
                Color(0xFF0B0804),
            ),
            glowColors = listOf(
                Color(0x33FFB341),
                Color(0x22FFB341),
                Color.Transparent,
            ),
            borderColors = listOf(
                Color(0xFF60401A),
                Color(0xFF35230E),
            ),
            accent = AccessDefaults.Amber,
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

@Composable
fun MoreDeckCard() {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(200.dp)
            .dottedBorder(
                color = AccessDefaults.TextMuted,
                shape = RoundedCornerShape(28.dp),
                strokeWidth = 1.dp,
                dotLength = 2.dp,
                gapLength = 4.dp,
            ),
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "+114",
                    fontFamily = JetbrainsMono,
                    color = AccessDefaults.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    text = "More in \nthe deck".uppercase(),
                    fontFamily = JetbrainsMono,
                    color = AccessDefaults.TextMuted,
                    fontSize = 11.sp,
                )
            }
        },
    )
}
