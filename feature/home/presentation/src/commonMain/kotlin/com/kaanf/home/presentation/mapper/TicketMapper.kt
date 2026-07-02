package com.kaanf.home.presentation.mapper

import com.kaanf.core.domain.model.ticket.EventTicketResponse
import com.kaanf.home.presentation.model.TicketQrUiModel
import com.kaanf.home.presentation.util.toClockText
import com.kaanf.home.presentation.util.toFormattedAddress
import com.kaanf.home.presentation.util.toQrDate
import kotlin.time.Clock

fun EventTicketResponse.toUiModel(): TicketQrUiModel {
    val deviceNowMillis = Clock.System.now().toEpochMilliseconds()

    return TicketQrUiModel(
        id = ticket.id,
        eventId = ticket.eventId,
        entryCode = ticket.entryCode,
        status = ticket.status.name,
        eventTitle = eventDetail.title,
        doorsOpenAt = eventDetail.doorsOpenAt.toEpochMilliseconds(),
        serverClockOffsetMillis = serverNow.toEpochMilliseconds() - deviceNowMillis,
        formattedVenueAddress = eventDetail.venue.toFormattedAddress(),
        formattedDoorTime = eventDetail.doorsOpenAt.toQrDate(),
        formattedDoorClock = eventDetail.doorsOpenAt.toClockText()
    )
}
