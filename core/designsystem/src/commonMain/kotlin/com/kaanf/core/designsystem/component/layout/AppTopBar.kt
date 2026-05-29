package com.kaanf.core.designsystem.component.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.ic_chevron_left_24
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    state: AppTopBarState = AppTopBarState.Title,
    title: String = "",
    rightText: String = "",
    onBackClick: (() -> Unit) = { },
    onRightClick: (() -> Unit) = {},
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when (state) {
                AppTopBarState.Title -> {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AccessDefaults.SurfaceElevated)
                            .border(
                                width = 1.dp,
                                color = AccessDefaults.BorderSoft,
                                shape = CircleShape,
                            )
                            .size(40.dp),
                    ) {
                        Icon(
                            painter = painterResource(AccessIcons.LeftChevron),
                            contentDescription = "Back",
                            tint = AccessDefaults.TextPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = AccessDefaults.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    TextButton(
                        onClick = onRightClick,
                        modifier = Modifier.widthIn(min = 36.dp),
                        contentPadding = PaddingValues(horizontal = 0.dp),
                    ) {
                        Text(
                            text = rightText,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = AccessDefaults.TextMuted,
                            ),
                        )
                    }
                }

                AppTopBarState.Dashboard -> {
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = onRightClick,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AccessDefaults.SurfaceElevated)
                            .border(
                                width = 1.dp,
                                color = AccessDefaults.BorderSoft,
                                shape = CircleShape,
                            )
                            .size(40.dp),
                    ) {
                        Icon(
                            painter = painterResource(AccessIcons.User),
                            contentDescription = "Back",
                            tint = AccessDefaults.TextPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AccessDefaults.Border),
        )
    }
}

@Composable
@Preview
fun AppTopBarPreview() {
    CrewTheme {
        AppTopBar(
            title = "Login",
            rightText = "Skip",
            state = AppTopBarState.Dashboard,
            onBackClick = {},
            onRightClick = {},
        )
    }
}
