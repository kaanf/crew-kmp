package com.kaanf.home.domain.usecase

import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.model.ticket.EventTicket
import com.kaanf.core.domain.model.ticket.EventTicketResponse
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.home.domain.repository.TicketRepository

class CreateTicketUseCase(
    private val ticketRepository: TicketRepository,
) {
    suspend operator fun invoke(eventId: EventId): Result<EventTicketResponse, DataError.Remote> {
        return ticketRepository.createTicket(eventId)
    }
}
