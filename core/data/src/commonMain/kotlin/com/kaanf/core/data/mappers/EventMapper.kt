package com.kaanf.core.data.mappers

import com.kaanf.core.data.dto.EventDashboardDto
import com.kaanf.core.data.dto.EventDetailDto
import com.kaanf.core.data.dto.PriceSerializable
import com.kaanf.core.domain.model.event.EventDashboard
import com.kaanf.core.domain.model.event.EventDetail
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
        hasMyTicket = hasMyTicket,
        featuredImageUrl = imageUrl,
    )
}

fun EventDetailDto.toDomain(): EventDetail {
    return EventDetail(
        id = id,
        title = title,
        description = description,
        venueId = venueId,
        venue = venue?.toDomain(),
        price = price.toDomain(),
        capacity = capacity,
        soldCount = soldCount,
        isFeatured = isFeatured,
        hasMyTicket = hasMyTicket,
        doorsOpenAt = Instant.parse(doorsOpenAt),
        startsAt = Instant.parse(startsAt),
        endsAt = Instant.parse(endsAt),
        status = EventStatus.valueOf(status),
        phase = phase,
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt),
        imageUrls = imageUrls,
        coverColors = coverColors.toArgbColors(),
    )
}

// All-or-nothing: dropping one bad entry would shift every later colour into the wrong role.
internal fun List<String>.toArgbColors(): List<Int> = map { hex ->
    val digits = hex.removePrefix("#")
    if (digits.length != 6 || !digits.all { it in '0'..'9' || it.lowercaseChar() in 'a'..'f' }) return emptyList()
    val rgb = digits.toInt(16)
    rgb or (0xFF shl 24)
}

fun PriceSerializable.toDomain(): Price {
    return Price(
        amount = (amount * 100).toLong(),
        currency = currency,
    )
}
