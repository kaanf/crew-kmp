package com.kaanf.home.data.repository

import com.kaanf.core.data.dto.EventDashboardDto
import com.kaanf.core.data.mappers.toDomain
import com.kaanf.core.data.networking.get
import com.kaanf.core.domain.model.event.EventDashboard
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.domain.util.map
import com.kaanf.home.domain.repository.EventRepository
import io.ktor.client.HttpClient

class EventRepositoryImpl(
    private val httpClient: HttpClient
): EventRepository {
    override suspend fun getEvents(): Result<List<EventDashboard>, DataError.Remote> {
        return httpClient.get<List<EventDashboardDto>>(
            route = "/events",
        ).map { events ->
            events.map { it.toDomain() }
        }
    }
}
