package com.kaanf.home.domain.usecase

import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.home.domain.model.EventGuest
import com.kaanf.home.domain.repository.EventRepository

class GetEventGuestsUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(eventId: EventId): Result<List<EventGuest>, DataError.Remote> {
        return eventRepository.getEventGuests(eventId)
    }
}
