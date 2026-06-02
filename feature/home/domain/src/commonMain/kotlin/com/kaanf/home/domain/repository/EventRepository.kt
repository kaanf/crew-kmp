package com.kaanf.home.domain.repository

import com.kaanf.core.domain.model.event.EventDashboard
import com.kaanf.core.domain.model.event.EventDetail
import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result

interface EventRepository {
    suspend fun getEvents(): Result<List<EventDashboard>, DataError.Remote>

    suspend fun getEventDetail(eventId: EventId): Result<EventDetail, DataError.Remote>
}
