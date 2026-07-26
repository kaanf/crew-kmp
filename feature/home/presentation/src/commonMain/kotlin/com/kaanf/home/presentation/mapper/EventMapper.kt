package com.kaanf.home.presentation.mapper

import com.kaanf.core.domain.model.event.EventDashboard
import com.kaanf.home.presentation.model.EventDashboardUiModel
import com.kaanf.home.presentation.util.toEventDateText

fun EventDashboard.toUiModel(): EventDashboardUiModel {
    return EventDashboardUiModel(
        id = id,
        title = title,
        isFeatured = isFeatured,
        date = startsAt.toEventDateText(),
        percentage = percentage,
        formattedPrice = price.format(),
        hasMyTicket = hasMyTicket,
        imageUrl = featuredImageUrl,
    )
}
