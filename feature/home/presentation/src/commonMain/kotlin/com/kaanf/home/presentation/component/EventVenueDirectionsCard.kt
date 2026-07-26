package com.kaanf.home.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.home.presentation.model.EventLocationUiModel
import com.kaanf.home.presentation.model.toDirectionsUri
import crew.feature.home.presentation.generated.resources.Res
import crew.feature.home.presentation.generated.resources.event_detail_directions_action
import org.jetbrains.compose.resources.stringResource

/** Mekân bilgisi ve telefonun harita uygulamasına götüren yol tarifi butonu. */
@Composable
internal fun EventVenueDirectionsCard(
    location: EventLocationUiModel,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AccessDefaults.Surface)
            .padding(16.dp),
    ) {
        Text(
            text = location.name,
            style = MaterialTheme.typography.titleSmall,
            color = AccessDefaults.TextPrimary,
        )
        Text(
            text = location.address,
            style = MaterialTheme.typography.bodySmall,
            color = AccessDefaults.TextSecondary,
        )
        BaseButton(
            text = stringResource(Res.string.event_detail_directions_action),
            onClick = { uriHandler.openUri(location.toDirectionsUri()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            filled = false,
        )
    }
}
