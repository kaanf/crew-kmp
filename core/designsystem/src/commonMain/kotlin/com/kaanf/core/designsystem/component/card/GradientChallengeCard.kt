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
import crew.core.designsystem.generated.resources.card_tall_bold
import crew.core.designsystem.generated.resources.card_tall_challenge
import crew.core.designsystem.generated.resources.card_tall_flirty
import crew.core.designsystem.generated.resources.card_tall_icebreaker
import crew.core.designsystem.generated.resources.card_tall_photo
import crew.core.designsystem.generated.resources.card_tall_storytime
import crew.core.designsystem.generated.resources.card_tall_team
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
    // Oyun ekranlarındaki dikey kağıt: metin tam genişlikte, illüstrasyon alt sağda.
    tall: Boolean = false,
    showPoints: Boolean = true,
    descriptionSpacing: Dp = if (emphasized) 8.dp else 6.dp,
    pressFeedbackEnabled: Boolean = true,
) {
    val shape = if (tall) ChallengeCardTallShape else ChallengeCardShape
    val surfaceModifier = if (pressFeedbackEnabled) {
        var isPressed by remember { mutableStateOf(false) }

        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.92f else 1f,
            animationSpec = tween(durationMillis = 120),
            label = "challenge_card_press_scale",
        )

        Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.shape = shape
                clip = true
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
    } else {
        // Display-only previews need neither a press gesture nor an animation.
        Modifier.clip(shape)
    }

    Box(
        modifier = modifier
            .width(width)
            .aspectRatio(if (tall) ChallengeCardTallAspectRatio else ChallengeCardAspectRatio)
            .then(surfaceModifier),
    ) {
        Image(
            painter = painterResource(card.variant.paper(tall)),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = width * 0.07f, vertical = width * if (tall) 0.07f else 0.05f),
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

            // Sağ alttaki illüstrasyonun üstüne taşmasın diye metin kartın sol ~%80'inde kalıyor;
            // dikey kartta illüstrasyon alt %30'da kaldığı için metin tam genişliği kullanıyor.
            Text(
                text = card.description,
                color = ChallengeCardInk,
                fontFamily = BricolageGrotesque,
                fontSize = if (tall) 24.sp else if (emphasized) 18.sp else 14.sp,
                maxLines = if (tall) 6 else 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = if (tall) 30.sp else if (emphasized) 22.sp else 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.2).sp,
                modifier = Modifier.fillMaxWidth(if (tall) 1f else 0.8f),
            )
        }
    }
}

// ponytail: Confession'ın kendi kağıdı yok; konuşma balonlu Storytime kağıdı ödünç alındı.
private fun ChallengeCardVariant.paper(tall: Boolean): DrawableResource = when (this) {
    ChallengeCardVariant.Icebreaker ->
        if (tall) Res.drawable.card_tall_icebreaker else Res.drawable.card_icebreaker

    ChallengeCardVariant.Team ->
        if (tall) Res.drawable.card_tall_team else Res.drawable.card_team

    ChallengeCardVariant.Storytime, ChallengeCardVariant.Confession ->
        if (tall) Res.drawable.card_tall_storytime else Res.drawable.card_storytime

    ChallengeCardVariant.Challenge ->
        if (tall) Res.drawable.card_tall_challenge else Res.drawable.card_challenge

    ChallengeCardVariant.Photo ->
        if (tall) Res.drawable.card_tall_photo else Res.drawable.card_photo

    ChallengeCardVariant.Bold ->
        if (tall) Res.drawable.card_tall_bold else Res.drawable.card_bold

    ChallengeCardVariant.Flirty ->
        if (tall) Res.drawable.card_tall_flirty else Res.drawable.card_flirty
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

/** Dikey kağıt kartların (card_tall_*.webp) en/boy oranı: 5:6. */
const val ChallengeCardTallAspectRatio = 5f / 6f
val ChallengeCardDefaultWidth = 260.dp
val ChallengeCardShape = RoundedCornerShape(8)

/** Dikey kağıtlar dolu dikdörtgen; köşeyi tek tip yuvarlasın diye burada kırpılıyor (%6, kısa kenara göre). */
val ChallengeCardTallShape = RoundedCornerShape(6)
private val ChallengeCardInk = Color(0xFFF4EEE6)
