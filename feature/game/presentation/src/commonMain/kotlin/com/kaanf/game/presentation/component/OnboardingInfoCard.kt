package com.kaanf.game.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.AccessShapes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.dottedBorder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OnboardingInfoCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dottedBorder(
                color = AccessDefaults.Border,
                shape = AccessShapes.Medium,
                strokeWidth = 1.dp,
                dotLength = 2.dp,
                gapLength = 4.dp,
                backgroundColor = Color.Transparent
            ),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "✨\t\tNew here? See how a night plays out",
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = AccessDefaults.TextSecondary,
                    )
                )

                Icon(
                    painter = painterResource(AccessIcons.RightChevron),
                    contentDescription = null,
                    tint = AccessDefaults.BorderSoft
                )
            }
        },
    )
}

@Composable
@Preview
fun OnboardingInfoCardPreview() {
    CrewTheme {
        OnboardingInfoCard()
    }
}
