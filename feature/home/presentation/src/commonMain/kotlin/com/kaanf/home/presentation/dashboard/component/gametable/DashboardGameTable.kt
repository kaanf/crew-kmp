package com.kaanf.home.presentation.dashboard.component.gametable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.card.ChallengeCardAspectRatio
import com.kaanf.core.designsystem.component.card.ChallengeCardShape
import com.kaanf.core.designsystem.component.card.GradientChallengeCard
import com.kaanf.core.designsystem.theme.Nunito
import com.kaanf.core.designsystem.theme.SpaceGrotesk
import com.kaanf.core.designsystem.theme.SpecialElite
import com.kaanf.core.presentation.model.ChallengeCardUiModel
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.dashboard_game_table_caption
import crew.feature.home.presentation.generated.resources.dashboard_game_table_cta
import crew.feature.home.presentation.generated.resources.dashboard_game_table_hint
import crew.feature.home.presentation.generated.resources.dashboard_game_table_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private val TableCream = Color(0xFFF3EBDC)
private val TableInk = Color(0xFF15110D)

/** Dashboard'un açık renkli "oyun masası": başlık, elle atılabilen soru kartı destesi ve CTA. */
@Composable
fun DashboardGameTable(
    cards: List<ChallengeCardUiModel>,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** Zemin saydam üst bar'ın arkasına uzanıyor; içerik bu kadar aşağıdan başlar. */
    topPadding: Dp = 0.dp,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TableCream)
            .padding(top = topPadding + 28.dp, bottom = 28.dp),
    ) {
        Text(
            text = stringResource(Res.string.dashboard_game_table_title),
            color = TableInk,
            fontFamily = Nunito,
            fontWeight = FontWeight.Black,
            fontSize = 46.sp,
            lineHeight = 44.sp,
            letterSpacing = (-1.5).sp,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        TableHint(modifier = Modifier.padding(start = 28.dp, top = 20.dp))

        SwipeableCardStack(
            cards = cards,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = stringResource(Res.string.dashboard_game_table_caption),
            color = TableInk,
            fontFamily = SpaceGrotesk,
            fontSize = 13.sp,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        )

        BaseButton(
            text = stringResource(Res.string.dashboard_game_table_cta),
            onClick = onCtaClick,
            filled = true,
            pill = true,
            backgroundColor = TableInk,
            borderColor = TableInk,
            contentColor = TableCream,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp),
        )
    }
}

@Composable
private fun TableHint(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.dashboard_game_table_hint),
            color = TableInk,
            fontFamily = SpecialElite,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            modifier = Modifier.rotate(-8f),
        )

        // El çizimi kıvrık ok: nota sağdan çıkıp desteye iniyor.
        Canvas(modifier = Modifier.size(width = 44.dp, height = 36.dp).offset(y = (-6).dp)) {
            val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
            val tip = Offset(size.width * 0.82f, size.height * 0.95f)
            drawPath(
                path = Path().apply {
                    moveTo(0f, size.height * 0.15f)
                    cubicTo(
                        size.width * 0.55f, -size.height * 0.1f,
                        size.width * 0.95f, size.height * 0.35f,
                        tip.x, tip.y,
                    )
                },
                color = TableInk,
                style = stroke,
            )
            drawLine(TableInk, tip, tip + Offset(-9.dp.toPx(), -6.dp.toPx()), stroke.width, StrokeCap.Round)
            drawLine(TableInk, tip, tip + Offset(4.dp.toPx(), -9.dp.toPx()), stroke.width, StrokeCap.Round)
        }
    }
}

/** Destedeki kartın masadaki duruşu; dx/dy kart boyutuna oranla. */
private data class CardPose(val rotation: Float, val dx: Float, val dy: Float)

private val StackPoses = listOf(
    CardPose(rotation = 2f, dx = 0f, dy = 0.12f),
    CardPose(rotation = 4f, dx = 0.07f, dy = -0.12f),
    CardPose(rotation = -9f, dx = -0.12f, dy = -0.02f),
)

/**
 * Üstteki kart her yöne sürüklenip eşiği geçince ekrandan atılır ve destenin en altına döner;
 * sonsuz döngü. Sürükleme okumaları graphicsLayer içinde, kaydırırken recomposition yok.
 */
@Composable
private fun SwipeableCardStack(
    cards: List<ChallengeCardUiModel>,
    modifier: Modifier = Modifier,
) {
    if (cards.isEmpty()) return

    var top by rememberSaveable { mutableIntStateOf(0) }
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val scope = rememberCoroutineScope()
    val depth = minOf(StackPoses.size, cards.size)

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val cardWidth = maxWidth * 0.82f
        val cardHeight = cardWidth / ChallengeCardAspectRatio
        val flingDistance = constraints.maxWidth.toFloat()
        val threshold = flingDistance * 0.25f

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight * 1.5f),
            contentAlignment = Alignment.Center,
        ) {
            // Arkadan öne çiziliyor; key kartın deste içi index'i, öne geçen kart yeniden oluşmuyor.
            for (level in depth - 1 downTo 0) {
                val index = (top + level) % cards.size
                key(index) {
                    GradientChallengeCard(
                        card = cards[index],
                        width = cardWidth,
                        emphasized = true,
                        showPoints = false,
                        descriptionSpacing = 4.dp,
                        modifier = Modifier
                            // Sürükleme yalnız üstteki kartta: destenin boş kalanında sayfa kaymaya devam ediyor.
                            // graphicsLayer'dan önce, drag miktarları kartın dönüşünden etkilenmesin.
                            .then(
                                if (level == 0) {
                                    Modifier.pointerInput(index) {
                                        detectDragGestures(
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                scope.launch { offset.snapTo(offset.value + dragAmount) }
                                            },
                                            onDragEnd = {
                                                scope.launch {
                                                    val current = offset.value
                                                    val distance = current.getDistance()
                                                    if (distance > threshold) {
                                                        offset.animateTo(current / distance * flingDistance * 1.5f, tween(240))
                                                        top = (top + 1) % cards.size
                                                        offset.snapTo(Offset.Zero)
                                                    } else {
                                                        offset.animateTo(
                                                            Offset.Zero,
                                                            spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                                        )
                                                    }
                                                }
                                            },
                                            onDragCancel = { scope.launch { offset.animateTo(Offset.Zero) } },
                                        )
                                    }
                                } else {
                                    Modifier
                                },
                            )
                            .graphicsLayer {
                                val progress = (offset.value.getDistance() / threshold).coerceAtMost(1f)
                                val pose = StackPoses[level]
                                // Üstteki kart atıldıkça arkadakiler bir üst pozisyona kayıyor.
                                val target = StackPoses[(level - 1).coerceAtLeast(0)]
                                val t = if (level == 0) 0f else progress

                                rotationZ = lerp(pose.rotation, target.rotation, t)
                                translationX = lerp(pose.dx, target.dx, t) * size.width
                                translationY = lerp(pose.dy, target.dy, t) * size.height

                                if (level == 0) {
                                    translationX += offset.value.x
                                    translationY += offset.value.y
                                    rotationZ += offset.value.x / flingDistance * 18f
                                }

                                shadowElevation = if (level == 0) 12.dp.toPx() else 4.dp.toPx()
                                shape = ChallengeCardShape
                                clip = false
                            },
                    )
                }
            }
        }
    }
}
