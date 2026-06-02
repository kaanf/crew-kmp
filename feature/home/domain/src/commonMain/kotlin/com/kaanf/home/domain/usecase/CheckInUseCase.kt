package com.kaanf.home.domain.usecase

import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.model.participant.CheckInResult
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.home.domain.repository.TicketRepository

class CheckInUseCase(
    private val ticketRepository: TicketRepository,
) {
    suspend operator fun invoke(
        eventId: EventId,
        entryCode: String,
    ): Result<CheckInResult, DataError.Remote> {
        return ticketRepository.checkIn(eventId = eventId, entryCode = entryCode)
    }
}
