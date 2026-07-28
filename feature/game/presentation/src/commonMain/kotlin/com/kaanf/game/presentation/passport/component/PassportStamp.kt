package com.kaanf.game.presentation.passport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.presentation.util.dottedBorder
import com.kaanf.game.presentation.passport.PassportStampUi
import com.kaanf.game.presentation.passport.StampShape
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_unknown_avatar_label
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Bir kişinin damgası: rengi, şekli ve eğimi o kişiye özel. Nadir damgalar
 * daha dolgun bir mürekkep zeminiyle ayrışır.
 */
@Composable
fun PassportStamp(
    stamp: PassportStampUi,
    modifier: Modifier = Modifier,
    initialTextSize: TextUnit = 18.sp,
    isSelected: Boolean = false,
) {
    val shape = stamp.shape.toShape()
    val rotation = stamp.rotationDegrees + if (stamp.shape == StampShape.Diamond) 45f else 0f

    Box(modifier = modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxSize(fraction = rotatedFitFraction(stamp.shape, rotation))
                .rotate(rotation),
        ) {
            if (isSelected) {
                Box(Modifier.fillMaxSize().border(2.dp, AccessDefaults.TextPrimary, shape))
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isSelected) 3.dp else 0.dp)
                    .background(
                        color = stamp.ink.copy(alpha = if (stamp.isRare) 0.14f else 0.09f),
                        shape = shape,
                    )
                    .border(width = 2.dp, color = stamp.ink, shape = shape)
                    .innerDashedRing(color = stamp.ink, shape = shape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stamp.initial,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = stamp.ink,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = initialTextSize,
                    ),
                    modifier = if (stamp.shape == StampShape.Diamond) {
                        Modifier.rotate(-45f)
                    } else {
                        Modifier
                    },
                )
            }
        }
    }
}

/**
 * Damganın hücresinin ne kadarını kaplayacağı. [Modifier.rotate] yalnızca çizimi döndürür,
 * layout kutusu döndürülmemiş haliyle kalır: eğik bir kare köşelerinden taşar ve komşu
 * damganın üstüne biner (45°'lik elmasta taşma hücrenin ~%21'i, yani griddeki boşluktan
 * büyük). Döndürülmüş kenarın kapladığı |cos|+|sin| oranı kadar küçültüp damgayı kendi
 * hücresinin içinde tutuyoruz; böylece griddeki boşluk gerçekten görünen boşluk oluyor.
 *
 * Daire dönmeye duyarsız olduğu için küçültülmez — yoksa gereksiz yere ufalırdı.
 */
private fun rotatedFitFraction(shape: StampShape, rotationDegrees: Float): Float {
    if (shape == StampShape.Round) return 1f
    val radians = rotationDegrees * PI.toFloat() / 180f
    return 1f / (abs(cos(radians)) + abs(sin(radians)))
}

@Composable
fun PassportEmptySlot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .dottedBorder(
                color = AccessDefaults.Border,
                shape = CircleShape,
                strokeWidth = 1.5.dp,
                dotLength = 5.dp,
                gapLength = 5.dp,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.match_unknown_avatar_label),
            style = MaterialTheme.typography.titleMedium.copy(
                color = AccessDefaults.TextFaint,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}

private fun StampShape.toShape(): Shape = when (this) {
    StampShape.Round -> CircleShape
    StampShape.Square -> RoundedCornerShape(24)
    StampShape.Notch -> RoundedCornerShape(
        topStartPercent = 50,
        topEndPercent = 16,
        bottomEndPercent = 50,
        bottomStartPercent = 16,
    )

    StampShape.Diamond -> RoundedCornerShape(18)
}

// Damganın içindeki kesikli mühür çizgisi; dottedBorder asimetrik köşeleri
// desteklemediği için outline üzerinden çizilir.
private fun Modifier.innerDashedRing(color: Color, shape: Shape): Modifier = drawBehind {
    val inset = 4.dp.toPx()
    val ringSize = Size(size.width - inset * 2, size.height - inset * 2)
    if (ringSize.minDimension <= 0f) return@drawBehind

    val dash = 4.dp.toPx()
    translate(left = inset, top = inset) {
        drawOutline(
            outline = shape.createOutline(ringSize, layoutDirection, this@drawBehind),
            color = color.copy(alpha = 0.55f),
            style = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash)),
            ),
        )
    }
}
