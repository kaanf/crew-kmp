package com.kaanf.game.presentation.quests

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import org.jetbrains.compose.resources.painterResource

private val PIN_SIZE = 22.dp

/** Oranla verilen noktayı, pinin merkezi o noktaya gelecek şekilde konumlandırır. */
internal fun Modifier.offsetPin(
    xFraction: Float,
    yFraction: Float,
    boxWidth: Dp,
    boxHeight: Dp,
): Modifier = offset(
    x = boxWidth * xFraction - PIN_SIZE / 2,
    y = boxHeight * yFraction - PIN_SIZE / 2,
)

/**
 * Fotoğrafın üzerine bırakılan etiket pini: ortasında sıra numarası, altında isim
 * (ve [onRemove] verilmişse ismin sağında etiketi kaldıran çarpı).
 *
 * [xFraction]/[yFraction] backend'le aynı sözleşme: sol üstten 0-1 oranı. Kapsayıcı
 * kutunun ölçüsü ([boxWidth]/[boxHeight]) dışarıdan gelir çünkü oranı piksele çeviren
 * tek yer burasıdır. Kutunun fotoğrafla birebir aynı olması şart (aspectRatio fotoğrafın
 * oranına eşit), yoksa pin kırpılmış alana göre kayar.
 */
@Composable
internal fun PhotoPin(
    xFraction: Float,
    yFraction: Float,
    number: Int,
    label: String,
    boxWidth: Dp,
    boxHeight: Dp,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.offsetPin(xFraction, yFraction, boxWidth, boxHeight),
    ) {
        Box(
            modifier = Modifier
                .size(PIN_SIZE)
                .background(AccessDefaults.Accent, CircleShape)
                .border(2.dp, Color.White.copy(alpha = 0.85f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$number",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = AccessDefaults.OnAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                ),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.62f), AccessShapes.Pill)
                .padding(start = 8.dp, end = if (onRemove != null) 4.dp else 8.dp, top = 3.dp, bottom = 3.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                ),
            )
            if (onRemove != null) {
                Icon(
                    painter = painterResource(AccessIcons.Close),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onRemove)
                        .padding(3.dp),
                )
            }
        }
    }
}
