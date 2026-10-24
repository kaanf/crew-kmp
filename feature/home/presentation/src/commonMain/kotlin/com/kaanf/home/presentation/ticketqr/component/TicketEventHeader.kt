package com.kaanf.home.presentation.ticketqr.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.image.BaseImage
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.home.presentation.model.EventDetailUiModel
import com.kaanf.home.presentation.model.TicketQrUiModel

/** Keeps the event identity visible while the door code remains the primary task. */
@Composable
internal fun TicketEventHeader(
    ticket: TicketQrUiModel,
    eventDetail: EventDetailUiModel?,
    compact: Boolean = false,
) {
    Row(
        modifier = Modifier.padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Reserve no space when artwork isn't available, including offline ticket access.
        eventDetail?.imageUrls?.firstOrNull()?.let { imageUrl ->
            BaseImage(
                imageUrl = imageUrl,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(if (compact) 48.dp else 80.dp).clip(RoundedCornerShape(16.dp)),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = ticket.eventTitle,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (compact) 20.sp else 30.sp,
                    lineHeight = if (compact) 24.sp else 32.sp,
                    letterSpacing = if (compact) (-0.4).sp else (-1).sp,
                ),
                color = AccessDefaults.TextPrimary,
            )
            Text(
                text = ticket.formattedDoorTime,
                style = MaterialTheme.typography.titleSmall,
                color = AccessDefaults.TextSecondary,
            )
            if (!compact) Text(
                text = ticket.formattedVenueAddress,
                style = MaterialTheme.typography.bodySmall,
                color = AccessDefaults.TextSecondary,
            )
        }
    }
}
