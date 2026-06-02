package com.kaanf.core.data.mappers

import com.kaanf.core.data.dto.EventTicketContextDto
import com.kaanf.core.data.dto.EventTicketDto
import com.kaanf.core.data.dto.EventTicketResponseDto
import com.kaanf.core.data.dto.VenueDto
import com.kaanf.core.domain.model.ticket.EventTicket
import com.kaanf.core.domain.model.ticket.EventTicketContext
import com.kaanf.core.domain.model.ticket.EventTicketResponse
import com.kaanf.core.domain.model.ticket.TicketStatus
import com.kaanf.core.domain.model.venue.Venue
import kotlin.time.Instant

fun EventTicketResponseDto.toDomain(): EventTicketResponse {
    return EventTicketResponse(
        ticket = ticket.toDomain(),
        eventDetail = eventDetail.toDomain(),
        serverNow = Instant.parse(serverNow)
    )
}

private fun EventTicketContextDto.toDomain(): EventTicketContext {
    return EventTicketContext(
        title = title,
        venue = venue.toDomain(),
        doorsOpenAt = Instant.parse(doorsOpenAt)
    )
}

private fun VenueDto.toDomain(): Venue {
    return Venue(
        id = id,
        name = name,
        district = district,
        address = address,
        city = city
    )
}

private fun EventTicketDto.toDomain(): EventTicket {
    return EventTicket(
        id = id,
        eventId = eventId,
        userId = userId,
        entryCode = entryCode,
        price = price.toDomain(),
        status = TicketStatus.valueOf(status),
        purchasedAt = Instant.parse(purchasedAt),
        checkedInAt = checkedInAt?.let { Instant.parse(it) },
    )
}
