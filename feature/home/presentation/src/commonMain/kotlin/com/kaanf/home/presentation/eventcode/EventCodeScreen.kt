package com.kaanf.home.presentation.eventcode

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.info.InfoCard
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.home.presentation.eventcode.component.CodeFieldStatus
import com.kaanf.home.presentation.eventcode.component.CodeInputField
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.event_code_headline
import crew.feature.home.presentation.generated.resources.event_code_helper_text
import crew.feature.home.presentation.generated.resources.event_code_info_description
import crew.feature.home.presentation.generated.resources.event_code_info_title
import crew.feature.home.presentation.generated.resources.event_code_input_hint
import crew.feature.home.presentation.generated.resources.event_code_show_qr_action
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EventCodeContent(
    eventCode: String,
    status: CodeFieldStatus,
    enabled: Boolean,
    onCodeChanged: (String) -> Unit,
    onShowQrClicked: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(Res.string.event_code_headline),
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = AccessDefaults.TextPrimary,
                    ),
                )

                Text(
                    text = stringResource(Res.string.event_code_helper_text),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextSecondary,
                        fontWeight = FontWeight.Medium,
                    ),
                )

                Spacer(modifier = Modifier.height(6.dp))

                CodeInputField(
                    value = eventCode,
                    onValueChange = onCodeChanged,
                    status = status,
                    enabled = enabled,
                )

                Text(
                    text = stringResource(Res.string.event_code_input_hint),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextMuted,
                    ),
                )

                Spacer(modifier = Modifier.height(6.dp))

                InfoCard(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = AccessDefaults.TextPrimary,
                                fontWeight = FontWeight.Bold,
                            ),
                        ) {
                            append(
                                stringResource(Res.string.event_code_info_title),
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
                                stringResource(Res.string.event_code_info_description)
                            )
                        }
                    },
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = AccessDefaults.Border,
                            shape = AccessShapes.Large,
                        )
                        .clickable(onClick = onShowQrClicked)
                        .padding(horizontal = 12.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally,
                    ),
                ) {
                    Icon(
                        painter = painterResource(AccessIcons.QR),
                        contentDescription = null,
                        tint = AccessDefaults.TextPrimary,
                        modifier = Modifier.size(16.dp),
                    )

                    Text(
                        text = stringResource(Res.string.event_code_show_qr_action),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = AccessDefaults.TextPrimary,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            }
        }
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
            onCodeChanged = {},
            onShowQrClicked = {},
            modifier = Modifier,
        )
    }
}
