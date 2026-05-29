package com.kaanf.home.domain.usecase

import com.kaanf.core.domain.model.event.EventDashboard
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.domain.util.map
import com.kaanf.home.domain.repository.EventRepository
import kotlin.time.Clock

class GetEventsUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(): Result<List<EventDashboard>, DataError.Remote> {
        return eventRepository.getEvents().map { events ->
            val now = Clock.System.now()
            events
                .filter { it.endsAt > now }
                .sortedBy { it.doorsAt }
        }
    }
}
