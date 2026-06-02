package com.kaanf.home.domain.repository

import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.model.participant.CheckInResult
import com.kaanf.core.domain.model.ticket.EventTicketResponse
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result

interface TicketRepository {
    suspend fun createTicket(eventId: EventId): Result<EventTicketResponse, DataError.Remote>

    suspend fun getMyTicket(eventId: EventId): Result<EventTicketResponse, DataError.Remote>

    suspend fun checkIn(
        eventId: EventId,
        entryCode: String,
    ): Result<CheckInResult, DataError.Remote>
}
