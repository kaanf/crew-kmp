package com.kaanf.home.presentation.ticketqr.component.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.ticket_qr_door_code_label
import crew.feature.home.presentation.generated.resources.ticket_qr_enter_event_code_action
import org.jetbrains.compose.resources.stringResource

// The same four colours used by Event Detail's numbered onboarding cards.
private val CodeCardColors = listOf(
    AccessDefaults.Accent,
    AccessDefaults.Blush,
    AccessDefaults.Sky,
    AccessDefaults.Gold,
)
private val CodeCardAngles = listOf(-6f, 3f, -3f, 6f)

@Composable
fun TicketQrInfoCard(
    entryCode: String,
    onEventCodeClicked: () -> Unit,
) {
    val codeLabel = stringResource(Res.string.ticket_qr_door_code_label)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = codeLabel,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                letterSpacing = (-0.6).sp,
            ),
            color = AccessDefaults.TextPrimary,
            modifier = Modifier.semantics { heading() },
        )

        // One read-only credential for accessibility, even when drawn as four playing cards.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .clearAndSetSemantics {
                    contentDescription = "$codeLabel: ${entryCode.toList().joinToString(" ")}"
                },
        ) {
            if (entryCode.length == 4) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    entryCode.forEachIndexed { index, character ->
                        CodePlayingCard(
                            character = character,
                            color = CodeCardColors[index],
                            angle = CodeCardAngles[index],
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            } else {
                // Preserve the complete credential if its server-side format changes.
                Text(
                    text = entryCode,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = BricolageGrotesque,
                        fontWeight = FontWeight.ExtraBold,
                        fontFeatureSettings = "tnum",
                    ),
                    autoSize = TextAutoSize.StepBased(minFontSize = 18.sp, maxFontSize = 64.sp),
                    maxLines = 1,
                    color = AccessDefaults.TextPrimary,
                    textAlign = TextAlign.Center,
                )
            }
        }

        BaseButton(
            text = stringResource(Res.string.ticket_qr_enter_event_code_action),
            onClick = onEventCodeClicked,
            filled = true,
            pill = true,
            backgroundColor = Color.White,
            borderColor = Color.White,
            contentColor = AccessDefaults.Background,
        )
    }
}

@Composable
private fun CodePlayingCard(
    character: Char,
    color: Color,
    angle: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(0.76f)
            .rotate(angle)
            .background(color, RoundedCornerShape(12.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = character.toString(),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.displayMedium.copy(
                fontFamily = BricolageGrotesque,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 44.sp,
                lineHeight = 48.sp,
                letterSpacing = 0.sp,
                fontFeatureSettings = "tnum",
            ),
            autoSize = TextAutoSize.StepBased(minFontSize = 20.sp, maxFontSize = 44.sp),
            maxLines = 1,
            textAlign = TextAlign.Center,
            color = AccessDefaults.OnAccent,
        )
    }
}

@Preview(name = "Door code · compact", widthDp = 280)
@Preview(name = "Door code · phone", widthDp = 360)
@Composable
private fun TicketQrInfoCardPreview() {
    CrewTheme {
        Column(
            modifier = Modifier.background(AccessDefaults.Background).padding(22.dp),
        ) {
            TicketQrInfoCard(entryCode = "4827", onEventCodeClicked = {})
        }
    }
}
