package com.kaanf.core.designsystem.component.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.designsystem.util.toCustomSnackbarVariant
import com.kaanf.core.presentation.model.SnackbarMessage
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.ic_snackbar_failure
import crew.core.designsystem.generated.resources.ic_snackbar_success
import crew.core.designsystem.generated.resources.ic_snackbar_warning
import crew.core.designsystem.generated.resources.snackbar_access_denied
import crew.core.designsystem.generated.resources.snackbar_uplink_failure
import crew.core.designsystem.generated.resources.snackbar_verification_complete
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class CustomSnackbarVariant(
    val titleRes: StringResource,
    val backgroundColor: Color,
    val borderColor: Color,
    val icon: DrawableResource,
) {
    Success(
        titleRes = Res.string.snackbar_verification_complete,
        backgroundColor = Color(0xFF0A1A0A),
        borderColor = Color(0xFF222222),
        icon = Res.drawable.ic_snackbar_success,
    ),
    Failure(
        titleRes = Res.string.snackbar_access_denied,
        backgroundColor = Color(0xFF1A0505),
        borderColor = Color(0xFF222222),
        icon = Res.drawable.ic_snackbar_failure,
    ),
    Warning(
        titleRes = Res.string.snackbar_uplink_failure,
        backgroundColor = Color(0xFF1A1500),
        borderColor = Color(0xFFD49D0C),
        icon = Res.drawable.ic_snackbar_warning,
    ),
}

private data class CustomSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val title: String? = null,
    val variant: CustomSnackbarVariant,
) : SnackbarVisuals

suspend fun SnackbarHostState.showSnackbar(
    title: String? = null,
    message: String,
    variant: CustomSnackbarVariant,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult =
    showSnackbar(
        visuals =
            CustomSnackbarVisuals(
                message = message,
                duration = duration,
                title = title,
                variant = variant,
            ),
    )

suspend fun SnackbarHostState.showSnackbar(
    snackbarMessage: SnackbarMessage,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult =
    showSnackbar(
        visuals =
            CustomSnackbarVisuals(
                message = snackbarMessage.description.asStringAsync(),
                duration = duration,
                title = snackbarMessage.title.asStringAsync(),
                variant = snackbarMessage.variant.toCustomSnackbarVariant(),
            ),
    )

@Composable
internal fun CustomSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
) {
    val visuals = snackbarData.visuals as? CustomSnackbarVisuals
    val variant = visuals?.variant ?: CustomSnackbarVariant.Warning
    val title = visuals?.title ?: stringResource(variant.titleRes)

    Row(
        modifier =
            modifier
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(12.dp),
                    clip = false,
                )
                .background(
                    color = AccessDefaults.SurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                )
                .border(
                    width = 1.dp,
                    color = AccessDefaults.BorderSoft,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(variant.icon),
            contentDescription = null,
            tint = Color.Unspecified,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextPrimary,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = snackbarData.visuals.message,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = AccessDefaults.TextSecondary,
                ),
            )
        }
    }
}

@Composable
@Preview
fun CustomSnackbarPreview() {
    CrewTheme {
        SnackbarHost(
            hostState = SnackbarHostState(),
            modifier =
                Modifier
                    .padding(top = 12.dp, start = 15.dp, end = 15.dp),
        ) { snackbarData ->
            CustomSnackbar(
                snackbarData = snackbarData,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
            )
        }
    }
}
