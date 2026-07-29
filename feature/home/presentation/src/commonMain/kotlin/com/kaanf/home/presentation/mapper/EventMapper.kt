package com.kaanf.home.presentation.mapper

import com.kaanf.core.domain.model.event.EventDashboard
import com.kaanf.home.presentation.model.EventDashboardUiModel
import com.kaanf.home.presentation.model.EventTiming
import com.kaanf.home.presentation.util.toClockText
import com.kaanf.home.presentation.util.toDayOfMonthText
import com.kaanf.home.presentation.util.toEventDateText
import com.kaanf.home.presentation.util.toMonthShortText
import kotlin.time.Clock
import kotlin.time.Instant

fun EventDashboard.toUiModel(now: Instant = Clock.System.now()): EventDashboardUiModel {
    return EventDashboardUiModel(
        id = id,
        title = title,
        isFeatured = isFeatured,
        date = startsAt.toEventDateText(),
        percentage = percentage,
        formattedPrice = price.format(),
        hasMyTicket = hasMyTicket,
        imageUrl = featuredImageUrl,
        dayOfMonth = startsAt.toDayOfMonthText(),
        monthShort = startsAt.toMonthShortText(),
        doorsTime = doorsAt.toClockText(),
        timing = when {
            now >= startsAt -> EventTiming.InGame
            now >= doorsAt -> EventTiming.DoorsOpen
            else -> EventTiming.BeforeDoors
        },
    )
}
