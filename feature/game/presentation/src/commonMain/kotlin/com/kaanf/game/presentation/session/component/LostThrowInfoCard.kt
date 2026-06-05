package com.kaanf.game.presentation.session.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import crew.feature.game.presentation.generated.resources.Res
import crew.feature.game.presentation.generated.resources.match_phase_lost_throw_description
import crew.feature.game.presentation.generated.resources.match_phase_lost_throw_icon
import crew.feature.game.presentation.generated.resources.match_phase_lost_throw_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LostThrowInfoCard(
    modifier: Modifier = Modifier,
) {
    val title = stringResource(Res.string.match_phase_lost_throw_title)
    val description = stringResource(Res.string.match_phase_lost_throw_description)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .drawWithCache {
                val shape = AccessShapes.Medium
                val outline = shape.createOutline(
                    size = size,
                    layoutDirection = layoutDirection,
                    density = this
                )

                val glowBrush = Brush.radialGradient(
                    colors = listOf(
                        AccessDefaults.Coral.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(0f, 0f),
                    radius = size.maxDimension * 0.9f
                )

                val borderBrush = Brush.radialGradient(
                    colors = listOf(
                        AccessDefaults.Coral.copy(alpha = 0.35f),
                        AccessDefaults.Surface.copy(alpha = 0.8f)
                    ),
                    center = Offset(0f, 0f),
                    radius = size.maxDimension * 0.9f
                )

                onDrawBehind {
                    drawOutline(
                        outline = outline,
                        color = AccessDefaults.Surface
                    )

                    drawOutline(
                        outline = outline,
                        brush = glowBrush
                    )

                    drawOutline(
                        outline = outline,
                        brush = borderBrush,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }
            .padding(
                all = 16.dp
            ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.match_phase_lost_throw_icon),
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
                        append(
                            title,
                        )
                    }

                    append("\n")

                    withStyle(
                        style = SpanStyle(
                            color = AccessDefaults.TextMuted,
                            fontWeight = FontWeight.Normal,
                        ),
                    ) {
                        append(
                            description,
                        )
                    }
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                ),
                textAlign = TextAlign.Start
            )
        }
    }
}


@Composable
@Preview
fun LostThrowInfoCardPreview() {
    CrewTheme {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            LostThrowInfoCard()
        }
    }
}
