package com.kaanf.home.domain.repository

import com.kaanf.core.domain.model.event.EventDashboard
import com.kaanf.core.domain.model.event.EventDetail
import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.home.domain.model.EventGuest

interface EventRepository {
    suspend fun resolveShareCode(code: String): Result<EventId, DataError.Remote>

    suspend fun getEvents(): Result<List<EventDashboard>, DataError.Remote>

    suspend fun getEventDetail(eventId: EventId): Result<EventDetail, DataError.Remote>

    suspend fun getEventGuests(eventId: EventId): Result<List<EventGuest>, DataError.Remote>
}
