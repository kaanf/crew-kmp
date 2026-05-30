package com.kaanf.home.presentation.gamelobby.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

data class FlowStep(
    val title: String,
    val time: String,
    val color: Color
)

@Composable
@Preview
fun TonightFlowCardPreview() {
    CrewTheme {
        TonightFlowCard()
    }
}

@Composable
fun TonightFlowCard(
    modifier: Modifier = Modifier,
    totalDuration: String = "2h 30m",
    steps: List<FlowStep> = listOf(
        FlowStep("Icebreaker", "20:30 – 21:00", Color(0xFFC8FF3D)),
        FlowStep("Social / Team", "21:00 – 22:00", Color(0xFF6FB7FF)),
        FlowStep("Bold", "22:00 – 22:45", Color(0xFFFF7A5C)),
        FlowStep("Final Round", "22:45 – 23:00", Color(0xFFFF5A7A)),
    )
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AccessDefaults.Surface,
                shape = AccessShapes.Card
            )
            .border(
                width = 1.dp,
                color = AccessDefaults.Border,
                shape = AccessShapes.Card
            )
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TONIGHT'S FLOW",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp
                ),
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = totalDuration,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                ),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        steps.forEachIndexed { index, step ->
            FlowStepRow(step = step)

            if (index != steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF251E18))
                )
            }
        }
    }
}

@Composable
private fun FlowStepRow(
    step: FlowStep,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlowyDot(color = step.color)

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            modifier = Modifier.weight(1f),
            text = step.title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextPrimary,
                fontSize = 14.sp
            )
        )

        Text(
            text = step.time,
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 14.sp,
                letterSpacing = -(0.2).sp
            )
        )
    }
}

@Composable
private fun GlowyDot(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(12.dp)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = 0.75f),
                            color.copy(alpha = 0.45f),
                            Color.Transparent
                        ),
                        radius = size.minDimension * 0.9f,
                        center = center
                    ),
                    radius = size.minDimension / 2f,
                    center = center
                )

                drawCircle(
                    color = color,
                    radius = 4.dp.toPx(),
                    center = center
                )
            }
    )
}
