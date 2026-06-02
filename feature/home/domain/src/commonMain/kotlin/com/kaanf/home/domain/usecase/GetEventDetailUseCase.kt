package com.kaanf.home.domain.usecase

import com.kaanf.core.domain.model.event.EventDetail
import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.home.domain.repository.EventRepository

class GetEventDetailUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(eventId: EventId): Result<EventDetail, DataError.Remote> {
        return eventRepository.getEventDetail(eventId)
    }
}
