package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.home.presentation.util.toMarkdownAnnotatedString
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun EventDetailInfoSection(
    description: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!description.isNullOrBlank()) {
            EventDetailBlock {
                Text(
                    text = remember(description) { description.toMarkdownAnnotatedString() },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AccessDefaults.TextPrimary,
                        lineHeight = 22.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun EventDetailBlock(
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(0.dp)) {
        content()
    }
}

@Composable
@Preview
private fun EventDetailInfoSectionPreview() {
    CrewTheme {
        Column(modifier = Modifier.background(AccessDefaults.Background).padding(22.dp)) {
            EventDetailInfoSection(
                description = "## The night 🎉\n" +
                    "Crew takes over the **whole bar** for one night. You get a deck of " +
                    "tasks, a card with your name on it, and _no seating plan_.\n" +
                    "- Doors 20:00\n" +
                    "- [House rules](https://crew.app/rules)",
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
