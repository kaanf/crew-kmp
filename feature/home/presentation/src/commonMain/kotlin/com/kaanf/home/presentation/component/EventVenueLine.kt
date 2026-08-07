package com.kaanf.home.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.BricolageGrotesque
import com.kaanf.home.presentation.model.EventLocationUiModel
import com.kaanf.home.presentation.model.toDirectionsUri

/** Adres satırı: dokunulunca telefonun harita uygulamasında yol tarifi açılır. */
@Composable
internal fun EventVenueLine(
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
