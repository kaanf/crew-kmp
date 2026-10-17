package com.kaanf.home.presentation.eventcode

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.kaanf.core.designsystem.component.info.InfoRow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.home.presentation.eventcode.component.CodeFieldStatus
import com.kaanf.home.presentation.eventcode.component.CodeInputField
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.event_code_headline
import crew.feature.home.presentation.generated.resources.ticket_qr_clear_code_action
import crew.feature.home.presentation.generated.resources.event_code_helper_text
import crew.feature.home.presentation.generated.resources.event_code_info_description
import crew.feature.home.presentation.generated.resources.event_code_info_title
import crew.feature.home.presentation.generated.resources.event_code_input_hint
import crew.feature.home.presentation.generated.resources.event_code_show_qr_action
import crew.feature.home.presentation.generated.resources.ticket_qr_doors_locked_description
import crew.feature.home.presentation.generated.resources.ticket_qr_doors_locked_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EventCodeContent(
    eventCode: String,
    status: CodeFieldStatus,
    enabled: Boolean,
    doorsLocked: Boolean,
    doorTime: String,
    onCodeChanged: (String) -> Unit,
    onClearClicked: () -> Unit,
    onShowQrClicked: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    eventHeader: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        eventHeader()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(Res.string.event_code_headline).replace("\n", " "),
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = BricolageGrotesque,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    letterSpacing = (-0.6).sp,
                ),
                color = AccessDefaults.TextPrimary,
            )
            Text(
                text = stringResource(Res.string.event_code_helper_text),
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                color = AccessDefaults.TextSecondary,
            )
        }

        if (doorsLocked) {
            InfoRow(
                title = stringResource(Res.string.ticket_qr_doors_locked_title),
                text = AnnotatedString(stringResource(Res.string.ticket_qr_doors_locked_description, doorTime)),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CodeInputField(
                value = eventCode,
                onValueChange = onCodeChanged,
                status = status,
                enabled = enabled,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.event_code_input_hint),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = AccessDefaults.TextMuted,
                )
                TextButton(
                    onClick = onClearClicked,
                    enabled = enabled && eventCode.isNotEmpty(),
                ) {
                    Text(
                        text = stringResource(Res.string.ticket_qr_clear_code_action),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (enabled && eventCode.isNotEmpty()) AccessDefaults.TextSecondary
                            else AccessDefaults.TextMuted.copy(alpha = 0.4f),
                    )
                }
            }
        }

        InfoRow(
            title = stringResource(Res.string.event_code_info_title),
            text = AnnotatedString(stringResource(Res.string.event_code_info_description)),
        )

        BaseButton(
            text = stringResource(Res.string.event_code_show_qr_action),
            onClick = onShowQrClicked,
            leadingIcon = AccessIcons.QR,
            pill = true,
        )
    }
}

@Composable
@Preview
fun EventCodePreview() {
    CrewTheme {
        EventCodeContent(
            eventCode = "",
            status = CodeFieldStatus.Editing,
            enabled = true,
            doorsLocked = false,
            doorTime = "20:00",
            onCodeChanged = {},
            onClearClicked = {},
            onShowQrClicked = {},
            modifier = Modifier,
        )
    }
}
