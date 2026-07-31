package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.designsystem.theme.JetbrainsMono
import com.kaanf.home.presentation.model.EventLocationUiModel
import com.kaanf.home.presentation.model.toDirectionsUri
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Hero'nun hemen altındaki gövde: meta satırı, katılım pill'i ve About / Where blokları. */
@Composable
internal fun EventDetailInfoSection(
    date: String,
    doorsTime: String,
    goingCount: Int,
    spotsLeft: Int,
    description: String?,
    location: EventLocationUiModel?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = listOfNotNull(date, doorsTime, location?.name).joinToString(SEPARATOR),
            style = MaterialTheme.typography.bodySmall.copy(color = AccessDefaults.TextSecondary),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        EventGoingPill(
            goingCount = goingCount,
            spotsLeft = spotsLeft,
            modifier = Modifier.padding(top = 13.dp),
        )

        if (!description.isNullOrBlank()) {
            EventDetailBlock(label = "About") {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextSecondary,
                        lineHeight = 22.sp,
                    ),
                )
            }
        }

        if (location != null) {
            EventDetailBlock(label = "Where", trailingLabel = location.district.uppercase()) {
                EventWhereLine(location = location)
            }
        }
    }
}

/** Blok başlığı: ince üst çizgi + mono etiket; sağda opsiyonel ikinci etiket. */
@Composable
private fun EventDetailBlock(
    label: String,
    trailingLabel: String? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 26.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AccessDefaults.BorderSoft),
        )

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

@Composable
private fun EventGoingPill(
    goingCount: Int,
    spotsLeft: Int,
    modifier: Modifier = Modifier,
) {
    val shape = CircleShape

    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = AccessDefaults.TextPrimary,
                    fontFamily = JetbrainsMono,
                    fontWeight = FontWeight.Bold,
                ),
            ) { append(goingCount.toString()) }
            append(" going · ")
            withStyle(SpanStyle(color = AccessDefaults.Coral)) { append("$spotsLeft left") }
        },
        style = MaterialTheme.typography.labelMedium.copy(
            color = AccessDefaults.TextSecondary,
            fontSize = 12.sp,
        ),
        modifier = modifier
            .clip(shape)
            .background(AccessDefaults.Surface)
            .border(1.dp, AccessDefaults.BorderSoft, shape)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    )
}

/** Adres satırı: dokunulunca telefonun harita uygulamasında yol tarifi açılır. */
@Composable
private fun EventWhereLine(
    location: EventLocationUiModel,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AccessDefaults.Surface)
            .border(1.dp, AccessDefaults.BorderSoft, shape)
            .clickable { uriHandler.openUri(location.toDirectionsUri()) }
            .padding(horizontal = 12.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(AccessDefaults.SurfaceElevated)
                .border(1.dp, AccessDefaults.BorderSoft, RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "📍", fontSize = 15.sp)
        }

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = BricolageGrotesque,
                    color = AccessDefaults.TextPrimary,
                    fontSize = 14.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = location.address,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.sp,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private const val SEPARATOR = " · "

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
                date = "30.05.2026, Saturday",
                doorsTime = "20:00",
                goingCount = 42,
                spotsLeft = 8,
                description = "Crew takes over the whole bar for one night. You get a deck of " +
                    "tasks, a card with your name on it, and no seating plan.",
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
