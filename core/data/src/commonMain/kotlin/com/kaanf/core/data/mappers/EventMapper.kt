package com.kaanf.core.data.mappers

import com.kaanf.core.data.dto.EventDashboardDto
import com.kaanf.core.data.dto.PriceSerializable
import com.kaanf.core.domain.model.event.EventDashboard
import com.kaanf.core.domain.model.event.EventStatus
import com.kaanf.core.domain.model.event.Price
import kotlin.time.Instant

fun EventDashboardDto.toDomain(): EventDashboard {
    return EventDashboard(
        id = id,
        title = title,
        venueId = venueId,
        percentage = percentage,
        price = price.toDomain(),
        isFeatured = isFeatured,
        doorsAt = Instant.parse(doorsAt),
        startsAt = Instant.parse(startsAt),
        endsAt = Instant.parse(endsAt),
        status = EventStatus.valueOf(status),
    )
}

fun PriceSerializable.toDomain(): Price {
    return Price(
        amount = (amount * 100).toLong(),
        currency = currency,
    )
}
