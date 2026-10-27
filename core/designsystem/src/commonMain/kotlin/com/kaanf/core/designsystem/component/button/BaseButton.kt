package com.kaanf.core.designsystem.component.button

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val DISABLED_BLEND = 0.5f

/**
 * Pasif görünümü [androidx.compose.ui.draw.alpha] ile değil, rengi zemine karıştırarak verir.
 * Alpha butonun arka planını da saydamlaştırdığı için, içeriğin üzerinde duran CTA'larda
 * (ör. etkinlik detayı) altta akan metin butonun içinden okunuyordu.
 *
 * ponytail: karışım zemini sabit [AccessDefaults.Background]. Surface üzerinde duran
 * yüzeylerde fark gözle ayırt edilmiyor; gerekirse zemin bir CompositionLocal'dan gelir.
 */
private fun Color.blendedIntoBackground(): Color =
    copy(alpha = DISABLED_BLEND).compositeOver(AccessDefaults.Background)

@Composable
fun BaseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    loadingText: String = "AUTHENTICATING...",
    filled: Boolean = false,
    leadingIcon: DrawableResource? = null,
    tintLeadingIcon: Boolean = true,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    contentColor: Color? = null,
    fillMaxWidth: Boolean = true,
    pill: Boolean = false,
) {
    val shape = if (pill) CircleShape else AccessShapes.Medium
    val borderWidth = 1.5.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isInteractionEnabled = enabled && !isLoading

    // Yükleme sırasında buton tam renkte kalır; yalnızca boştaki pasif buton soluklaşır.
    val isDimmed = !isInteractionEnabled && !isLoading

    val resolvedBorderColor = (borderColor ?: if (filled) AccessDefaults.Accent else AccessDefaults.Border)
        .let { if (isDimmed) it.blendedIntoBackground() else it }
    val resolvedBackgroundColor = (backgroundColor ?: if (filled) AccessDefaults.Accent else AccessDefaults.Surface)
        .let { if (isDimmed) it.blendedIntoBackground() else it }
    val resolvedContentColor = contentColor
        ?: when {
            filled -> AccessDefaults.OnAccent
            isDimmed -> AccessDefaults.TextMuted
            else -> AccessDefaults.TextPrimary
        }

    Box(
        modifier =
            modifier
                .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
                .height(52.dp)
                // Zemin ve kenarlık tek kutuda: kenarlık eskiden tüm alanı boyayan ayrı bir
                // Box'tı, arkasındaki zemin kutusuyla birlikte her butonu iki kez dolduruyordu.
                .clip(shape)
                .background(resolvedBackgroundColor)
                .border(borderWidth, resolvedBorderColor, shape)
                .clickable(
                    enabled = isInteractionEnabled,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                // Wrap mode: genişlik içeriği takip eder (metin ↔ loader); ortadan büyüyüp küçülür.
                .then(if (fillMaxWidth) Modifier else Modifier.animateContentSize(alignment = Alignment.Center))
                .padding(horizontal = if (fillMaxWidth) 16.dp else 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = resolvedContentColor,
                strokeWidth = 2.dp,
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingIcon != null) {
                    if (tintLeadingIcon) {
                        Icon(
                            painter = painterResource(leadingIcon),
                            contentDescription = null,
                            tint = resolvedContentColor,
                            modifier = Modifier.size(20.dp),
                        )
                    } else {
                        Image(
                            painter = painterResource(leadingIcon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = resolvedContentColor,
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun BaseButtonPreview() {
    CrewTheme(isDarkTheme = true) {
        Column(
            modifier =
                Modifier
                    .background(Color(0xFF0E0B08))
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BaseButton(
                text = "Primary",
                onClick = {},
                filled = true,
            )
            BaseButton(
                text = "Secondary",
                onClick = {},
            )
            BaseButton(
                text = "With icon",
                onClick = {},
                filled = true,
                leadingIcon = AccessIcons.QR,
            )
            BaseButton(
                text = "Disabled secondary",
                onClick = {},
                enabled = false,
            )
            BaseButton(
                text = "Disabled primary",
                onClick = {},
                enabled = false,
                filled = true,
            )
            BaseButton(
                text = "Loading",
                onClick = {},
                isLoading = true,
                loadingText = "Loading...",
                filled = true,
            )
        }
    }
}
