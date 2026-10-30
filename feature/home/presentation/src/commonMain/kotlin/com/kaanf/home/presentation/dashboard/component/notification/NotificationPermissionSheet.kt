package com.kaanf.home.presentation.dashboard.component.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.notification_sheet_description
import crew.feature.home.presentation.generated.resources.notification_sheet_done
import crew.feature.home.presentation.generated.resources.notification_sheet_enable
import crew.feature.home.presentation.generated.resources.notification_sheet_off_description
import crew.feature.home.presentation.generated.resources.notification_sheet_off_title
import crew.feature.home.presentation.generated.resources.notification_sheet_on_description
import crew.feature.home.presentation.generated.resources.notification_sheet_on_title
import crew.feature.home.presentation.generated.resources.notification_sheet_preview_body
import crew.feature.home.presentation.generated.resources.notification_sheet_preview_time
import crew.feature.home.presentation.generated.resources.notification_sheet_preview_title
import crew.feature.home.presentation.generated.resources.notification_sheet_skip
import crew.feature.home.presentation.generated.resources.notification_sheet_title
import crew.feature.home.presentation.generated.resources.notification_sheet_title_accent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class NotificationSheetStep { Ask, Enabled, Skipped }

/** Sistem izin dialogundan önce gösterilen açıklama sheet'i; dialog tek haklık olduğu için önce niyet sorulur. */
@Composable
fun NotificationPermissionSheet(
    step: NotificationSheetStep,
    onEnableClick: () -> Unit,
    onSkipClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    ContainerBottomSheet(onDismiss = onDismiss) {
        when (step) {
            NotificationSheetStep.Ask -> AskContent(onEnableClick = onEnableClick, onSkipClick = onSkipClick)
            NotificationSheetStep.Enabled,
            NotificationSheetStep.Skipped -> ResultContent(
                isEnabled = step == NotificationSheetStep.Enabled,
                onDoneClick = onDismiss,
            )
        }
    }
}

@Composable
private fun AskContent(
    onEnableClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NotificationPreviewStack()

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = buildAnnotatedString {
                    appendLine(stringResource(Res.string.notification_sheet_title))
                    withStyle(SpanStyle(color = AccessDefaults.Accent)) {
                        append(stringResource(Res.string.notification_sheet_title_accent))
                    }
                },
                style = MaterialTheme.typography.displaySmall.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 31.sp,
                    letterSpacing = (-1).sp,
                    textAlign = TextAlign.Center,
                ),
            )

            SheetDescription(text = stringResource(Res.string.notification_sheet_description))
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BaseButton(
                text = stringResource(Res.string.notification_sheet_enable),
                onClick = onEnableClick,
                filled = true,
            )

            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(role = Role.Button, onClick = onSkipClick)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.notification_sheet_skip),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
    }
}

/** Tasarımdaki iOS bildirim önizlemesi: öndeki kart + arkasında iki silik katman. */
@Composable
private fun NotificationPreviewStack() {
    Box(modifier = Modifier.fillMaxWidth()) {
        StackLayer(horizontalInset = 36.dp, bottomInset = 0.dp, cornerRadius = 16.dp, alpha = 0.55f)
        StackLayer(horizontalInset = 18.dp, bottomInset = 14.dp, cornerRadius = 18.dp, alpha = 0.85f)

        Row(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 28.dp)
                .fillMaxWidth()
                .background(AccessDefaults.SurfaceElevated, RoundedCornerShape(18.dp))
                .border(1.dp, AccessDefaults.Border, RoundedCornerShape(18.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(AccessIcons.AppIcon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp)),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(Res.string.notification_sheet_preview_title),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AccessDefaults.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    Text(
                        text = stringResource(Res.string.notification_sheet_preview_time),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AccessDefaults.TextMuted,
                            fontSize = 12.sp,
                        ),
                    )
                }

                Text(
                    text = stringResource(Res.string.notification_sheet_preview_body),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun BoxScope.StackLayer(
    horizontalInset: Dp,
    bottomInset: Dp,
    cornerRadius: Dp,
    alpha: Float,
) {
    val shape = RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(start = horizontalInset, end = horizontalInset, bottom = bottomInset)
            .fillMaxWidth()
            .height(48.dp)
            .alpha(alpha)
            .background(AccessDefaults.SurfaceHigh, shape)
            .border(1.dp, AccessDefaults.BorderSoft, shape),
    )
}

@Composable
private fun ResultContent(
    isEnabled: Boolean,
    onDoneClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(AccessDefaults.Accent.copy(alpha = 0.12f), CircleShape)
                .border(1.dp, AccessDefaults.Accent.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(AccessIcons.Check),
                contentDescription = null,
                tint = AccessDefaults.Accent,
                modifier = Modifier.size(24.dp),
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(
                    if (isEnabled) Res.string.notification_sheet_on_title else Res.string.notification_sheet_off_title,
                ),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = AccessDefaults.TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp,
                    letterSpacing = (-0.8).sp,
                    textAlign = TextAlign.Center,
                ),
            )

            SheetDescription(
                text = stringResource(
                    if (isEnabled) Res.string.notification_sheet_on_description else Res.string.notification_sheet_off_description,
                ),
            )
        }

        BaseButton(
            text = stringResource(Res.string.notification_sheet_done),
            onClick = onDoneClick,
        )
    }
}

@Composable
private fun SheetDescription(text: String) {
    Text(
        text = text,
        modifier = Modifier.widthIn(max = 300.dp),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = AccessDefaults.TextSecondary,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center,
        ),
    )
}

@Preview
@Composable
private fun AskContentPreview() {
    CrewTheme {
        AskContent(onEnableClick = {}, onSkipClick = {})
    }
}

@Preview
@Composable
private fun ResultContentPreview() {
    CrewTheme {
        ResultContent(isEnabled = true, onDoneClick = {})
    }
}
