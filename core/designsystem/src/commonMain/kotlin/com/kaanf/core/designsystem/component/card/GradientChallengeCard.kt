package com.kaanf.core.designsystem.component.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.presentation.model.ChallengeCardUiModel
import com.kaanf.core.presentation.model.ChallengeCardVariant
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.card_bold
import crew.core.designsystem.generated.resources.card_challenge
import crew.core.designsystem.generated.resources.card_flirty
import crew.core.designsystem.generated.resources.card_icebreaker
import crew.core.designsystem.generated.resources.card_photo
import crew.core.designsystem.generated.resources.card_storytime
import crew.core.designsystem.generated.resources.card_team
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun GradientChallengeCard(
    modifier: Modifier = Modifier,
    card: ChallengeCardUiModel,
    width: Dp = ChallengeCardDefaultWidth,
    // Oyun ekranlarında kart tek başına sahnede: görev metni büyük ve kalın, okunması tek bakışta.
    emphasized: Boolean = false,
    showPoints: Boolean = true,
    descriptionSpacing: Dp = if (emphasized) 8.dp else 6.dp,
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "challenge_card_press_scale"
    )

    Box(
        modifier = modifier
            .width(width)
            .aspectRatio(ChallengeCardAspectRatio)
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
            .clip(ChallengeCardShape),
    ) {
        Image(
            painter = painterResource(card.variant.paper),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = width * 0.07f, vertical = width * 0.05f),
            verticalArrangement = Arrangement.spacedBy(descriptionSpacing),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = card.variant.name.uppercase(),
                    color = card.variant.labelColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (emphasized) 12.sp else 10.sp,
                    letterSpacing = 2.sp,
                )

                if (showPoints) {
                    Text(
                        text = "+${card.points} PTS",
                        color = card.variant.labelColor,
                        fontSize = if (emphasized) 13.sp else 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            // Sağ alttaki illüstrasyonun üstüne taşmasın diye metin kartın sol ~%80'inde kalıyor.
            Text(
                text = card.description,
                color = ChallengeCardInk,
                fontFamily = BricolageGrotesque,
                fontSize = if (emphasized) 18.sp else 14.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = if (emphasized) 22.sp else 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.2).sp,
                modifier = Modifier.fillMaxWidth(0.8f),
            )
        }
    }
}

// ponytail: Confession'ın kendi kağıdı yok; konuşma balonlu Storytime kağıdı ödünç alındı.
private val ChallengeCardVariant.paper: DrawableResource
    get() = when (this) {
        ChallengeCardVariant.Icebreaker -> Res.drawable.card_icebreaker
        ChallengeCardVariant.Team -> Res.drawable.card_team
        ChallengeCardVariant.Storytime, ChallengeCardVariant.Confession -> Res.drawable.card_storytime
        ChallengeCardVariant.Challenge -> Res.drawable.card_challenge
        ChallengeCardVariant.Photo -> Res.drawable.card_photo
        ChallengeCardVariant.Bold -> Res.drawable.card_bold
        ChallengeCardVariant.Flirty -> Res.drawable.card_flirty
    }

private val ChallengeCardVariant.labelColor: Color
    get() = when (this) {
        ChallengeCardVariant.Icebreaker -> Color(0xFFD0E085)
        ChallengeCardVariant.Team -> Color(0xFF83CEBA)
        ChallengeCardVariant.Storytime -> Color(0xFF9CA5FA)
        ChallengeCardVariant.Challenge -> Color(0xFFEA75C3)
        ChallengeCardVariant.Photo -> Color(0xFF81A6F9)
        ChallengeCardVariant.Bold -> Color(0xFFFCA97D)
        ChallengeCardVariant.Confession -> Color(0xFFB9A2F0)
        ChallengeCardVariant.Flirty -> Color(0xFFE986AD)
    }

/** Kağıt kart görsellerinin (card_*.webp) en/boy oranı. */
const val ChallengeCardAspectRatio = 1.95f
val ChallengeCardDefaultWidth = 260.dp
val ChallengeCardShape = RoundedCornerShape(8)
private val ChallengeCardInk = Color(0xFFF4EEE6)
