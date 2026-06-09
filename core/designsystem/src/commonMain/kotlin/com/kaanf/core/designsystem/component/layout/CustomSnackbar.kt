package com.kaanf.core.designsystem.component.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.UIText
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.ic_bolt
import crew.core.designsystem.generated.resources.ic_check
import crew.core.designsystem.generated.resources.ic_close
import crew.core.designsystem.generated.resources.ic_info
import crew.core.designsystem.generated.resources.ic_user
import crew.core.designsystem.generated.resources.ic_wifi_off
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

data class SnackbarMessage(
    val title: UIText,
    val description: UIText,
    val variant: SnackbarVariant
)

enum class SnackbarVariant(
    val icon: DrawableResource,
) {
    Accent(
        icon = Res.drawable.ic_bolt
    ),
    Success(
        icon = Res.drawable.ic_check
    ),
    Info(
        icon = Res.drawable.ic_info
    ),
    Warn(
        icon = Res.drawable.ic_wifi_off
    ),
    Error(
        icon = Res.drawable.ic_close
    ),
    AccentALT(
        icon = Res.drawable.ic_user
    )
}

private data class CustomSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val title: String? = null,
    val variant: SnackbarVariant,
) : SnackbarVisuals

suspend fun SnackbarHostState.showSnackbar(
    snackbarMessage: SnackbarMessage,
): SnackbarResult =
    showSnackbar(
        visuals =
            CustomSnackbarVisuals(
                title = snackbarMessage.title.asStringAsync(),
                message = snackbarMessage.description.asStringAsync(),
                variant = snackbarMessage.variant,
                duration = SnackbarDuration.Short,
            ),
    )

@Composable
internal fun CustomSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
) {
    val visuals = snackbarData.visuals as? CustomSnackbarVisuals
    val variant = visuals?.variant?.icon
    val title = visuals?.title

    Row(
        modifier =
            modifier
                .shadow(
                    elevation = 24.dp,
                    shape = AccessShapes.Medium,
                    clip = false,
                )
                .background(
                    color = AccessDefaults.Surface,
                    shape = AccessShapes.Medium,
                )
                .border(
                    width = 1.dp,
                    color = AccessDefaults.BorderSoft,
                    shape = AccessShapes.Medium,
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (variant != null) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(variant),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (!title.isNullOrEmpty()) {
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
