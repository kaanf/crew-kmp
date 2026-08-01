package com.kaanf.core.designsystem.component.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Ortalanmış ekran/faz başlığı: küçük üst etiket (eyebrow) + başlık + açıklama.
 * Üçü de aynı hizada durur, verilmeyen satır hiç çizilmez.
 */
@Composable
fun SectionHeader(
    title: AnnotatedString,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    eyebrowColor: Color = AccessDefaults.TextMuted,
    description: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge,
    verticalSpacing: Dp = 8.dp,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        eyebrow?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = eyebrowColor,
                    fontSize = 12.sp,
                ),
            )
        }

        Text(
            text = title,
            style = titleStyle,
            textAlign = TextAlign.Center,
        )

        description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextSecondary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    eyebrowColor: Color = AccessDefaults.TextMuted,
    description: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge,
    verticalSpacing: Dp = 8.dp,
) {
    SectionHeader(
        title = AnnotatedString(title),
        modifier = modifier,
        eyebrow = eyebrow,
        eyebrowColor = eyebrowColor,
        description = description,
        titleStyle = titleStyle,
        verticalSpacing = verticalSpacing,
    )
}

@Preview
@Composable
private fun SectionHeaderPreview() {
    CrewTheme {
        SectionHeader(
            eyebrow = "ROUND 3",
            title = "Who won?",
            description = "Tap what actually happened. Mira sees the same two options.",
        )
    }
}
