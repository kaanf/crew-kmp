package com.kaanf.core.designsystem.component.mediapicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ImageSourceBottomSheet(
    onTakePhoto: () -> Unit,
    onChooseFromGallery: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ContainerBottomSheet(
        onDismiss = onDismiss,
        dismissible = true,
        showDragHandle = true,
        content = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Add a photo",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = AccessDefaults.TextPrimary,
                        textAlign = TextAlign.Start,
                    ),
                )

                Text(
                    text = "Take a new photo or choose one from your gallery.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextSecondary,
                        fontSize = 13.sp,
                    ),
                )

                Spacer(modifier = Modifier.height(4.dp))

                ImageSourceRow(
                    icon = AccessIcons.Camera,
                    title = "Take a photo",
                    subtitle = "Use your camera",
                    onClick = onTakePhoto,
                )

                ImageSourceRow(
                    icon = AccessIcons.Image,
                    title = "Choose from gallery",
                    subtitle = "Pick an existing photo",
                    onClick = onChooseFromGallery,
                )
            }
        },
    )
}

@Composable
private fun ImageSourceRow(
    icon: DrawableResource,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .background(AccessDefaults.SurfaceElevated, shape)
            .border(width = 1.dp, color = AccessDefaults.Border, shape = shape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AccessDefaults.AccentFocusBg, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(icon),
                tint = AccessDefaults.TextPrimary,
                contentDescription = null,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(color = AccessDefaults.TextPrimary),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextSecondary,
                    fontSize = 12.sp,
                ),
            )
        }

        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(AccessIcons.RightChevron),
            tint = AccessDefaults.TextFaint,
            contentDescription = null,
        )
    }
}

@Composable
@Preview
fun ImageSourceBottomSheetPreview() {
    CrewTheme {
        ImageSourceBottomSheet(
            onTakePhoto = {},
            onChooseFromGallery = {},
            onDismiss = {}
        )
    }
}
