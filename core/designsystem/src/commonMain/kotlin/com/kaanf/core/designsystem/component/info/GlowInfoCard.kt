package com.kaanf.core.designsystem.component.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Sol üst köşeden yayılan renkli parıltı + aynı gradyanla çizilen kenarlık taşıyan uyarı kartı.
 * [accentColor] kartın tonunu belirler: Amber "kapılar kapandı", Coral "atışı kaybettin" gibi.
 * Parıltı çizimi [drawWithCache] içinde kalır; boyut değişmedikçe brush yeniden kurulmaz.
 */
@Composable
fun GlowInfoCard(
    emoji: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    accentColor: Color = AccessDefaults.Amber,
    glowAlpha: Float = 0.1f,
    borderAlpha: Float = 0.25f,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .drawWithCache {
                val shape = AccessShapes.Medium
                val outline = shape.createOutline(
                    size = size,
                    layoutDirection = layoutDirection,
                    density = this,
                )

                val glowBrush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = glowAlpha),
                        Color.Transparent,
                    ),
                    center = Offset(0f, 0f),
                    radius = size.maxDimension * 0.9f,
                )

                val borderBrush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = borderAlpha),
                        AccessDefaults.Surface.copy(alpha = 0.8f),
                    ),
                    center = Offset(0f, 0f),
                    radius = size.maxDimension * 0.9f,
                )

                onDrawBehind {
                    drawOutline(outline = outline, color = AccessDefaults.Surface)
                    drawOutline(outline = outline, brush = glowBrush)
                    drawOutline(
                        outline = outline,
                        brush = borderBrush,
                        style = Stroke(width = 1.dp.toPx()),
                    )
                }
            }
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp,
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = AccessDefaults.TextPrimary,
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append(title)
                    }

                    append("\n")

                    withStyle(
                        style = SpanStyle(
                            color = AccessDefaults.TextMuted,
                            fontWeight = FontWeight.Normal,
                        ),
                    ) {
                        append(description)
                    }
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                ),
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Preview
@Composable
private fun GlowInfoCardPreview() {
    CrewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GlowInfoCard(
                emoji = "🔒",
                title = "Doors are locked",
                description = "Your QR wakes up at 20:00. Grab a drink until then.",
            )
            GlowInfoCard(
                emoji = "✊",
                title = "You lost the throw",
                description = "The winner picks a task for you. Accepting is the fast way out.",
                accentColor = AccessDefaults.Coral,
                glowAlpha = 0.15f,
                borderAlpha = 0.45f,
            )
        }
    }
}
