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
import com.kaanf.home.presentation.component.EventVenueLine
import com.kaanf.home.presentation.model.EventLocationUiModel
import com.kaanf.home.presentation.util.toMarkdownAnnotatedString
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Hero'nun hemen altındaki gövde: About / Where blokları. */
@Composable
internal fun EventDetailInfoSection(
    description: String?,
    location: EventLocationUiModel?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!description.isNullOrBlank()) {
            EventDetailBlock(label = "About", divider = false) {
                Text(
                    text = remember(description) { description.toMarkdownAnnotatedString() },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextSecondary,
                        lineHeight = 22.sp,
                    ),
                )
            }
        }

        if (location != null) {
            EventDetailBlock(
                label = "Where",
                trailingLabel = location.district.uppercase(),
                // İlk blok hero'nun hemen altında; ayırıcı ancak üstünde başka blok varsa anlamlı.
                divider = !description.isNullOrBlank(),
            ) {
                EventVenueLine(location = location)
            }
        }
    }
}

/** Blok başlığı: ince üst çizgi + mono etiket; sağda opsiyonel ikinci etiket. */
@Composable
private fun EventDetailBlock(
    label: String,
    trailingLabel: String? = null,
    divider: Boolean = true,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = if (divider) 26.dp else 0.dp)) {
        if (divider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AccessDefaults.BorderSoft),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label.uppercase(), style = BlockLabelStyle)
            if (trailingLabel != null) {
                Text(text = trailingLabel, style = BlockLabelStyle)
            }
        }

        content()
    }
}

private val BlockLabelStyle
    @Composable get() = MaterialTheme.typography.labelSmall.copy(
        color = AccessDefaults.TextMuted,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.2.sp,
    )

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
                location = EventLocationUiModel(
                    name = "Bar Skutek",
                    address = "Štěpánská 20, Vinohrady / Praha",
                    district = "Vinohrady",
                    latitude = 50.0,
                    longitude = 14.4,
                ),
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
