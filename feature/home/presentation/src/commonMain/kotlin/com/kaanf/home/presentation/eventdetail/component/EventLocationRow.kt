package com.kaanf.home.presentation.eventdetail.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.AccessIcons
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.home.presentation.model.EventLocationUiModel
import com.kaanf.home.presentation.model.toDirectionsUri
import org.jetbrains.compose.resources.painterResource

/** Konum ikonu, mekân adı + ilçe/şehir; sağdaki ok ikonu Google Maps'i açar. */
@Composable
internal fun EventLocationRow(
    location: EventLocationUiModel,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(location.toDirectionsUri()) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(AccessIcons.Pin),
            contentDescription = null,
            tint = AccessDefaults.TextPrimary,
            modifier = Modifier.size(22.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = BricolageGrotesque,
                    color = AccessDefaults.TextPrimary,
                    fontSize = 15.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = "${location.district}, ${location.city}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextSecondary,
                    fontSize = 12.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Icon(
            painter = painterResource(AccessIcons.ArrowUpRight),
            contentDescription = null,
            tint = AccessDefaults.TextPrimary,
            modifier = Modifier.size(22.dp),
        )
    }
}
