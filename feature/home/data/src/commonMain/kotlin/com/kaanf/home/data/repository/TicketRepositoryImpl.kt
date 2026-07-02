package com.kaanf.home.data.repository

import com.kaanf.core.data.dto.CheckInRequestDto
import com.kaanf.core.data.dto.CheckInResultDto
import com.kaanf.core.data.dto.EventTicketResponseDto
import com.kaanf.core.data.mappers.toDomain
import com.kaanf.core.data.networking.get
import com.kaanf.core.data.networking.post
import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.model.participant.CheckInResult
import com.kaanf.core.domain.model.ticket.EventTicketResponse
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.data.networking.mapCatching
import com.kaanf.home.domain.repository.TicketRepository
import io.ktor.client.HttpClient

class TicketRepositoryImpl(
    private val httpClient: HttpClient,
) : TicketRepository {
    override suspend fun createTicket(eventId: EventId): Result<EventTicketResponse, DataError.Remote> {
        return httpClient.post<EventTicketResponseDto>(
            route = "/events/$eventId/tickets",
        ).mapCatching { it.toDomain() }
    }

    override suspend fun getMyTicket(eventId: EventId): Result<EventTicketResponse, DataError.Remote> {
        return httpClient.get<EventTicketResponseDto>(
            route = "/events/$eventId/my-ticket",
        ).mapCatching { it.toDomain() }
    }

    override suspend fun checkIn(
        eventId: EventId,
        entryCode: String,
    ): Result<CheckInResult, DataError.Remote> {
        return httpClient.post<CheckInRequestDto, CheckInResultDto>(
            route = "/events/$eventId/check-in",
            body = CheckInRequestDto(entryCode = entryCode),
        ).mapCatching { it.toDomain() }
    }
}
