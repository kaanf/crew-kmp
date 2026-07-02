package com.kaanf.home.data.repository

import com.kaanf.core.data.dto.EventDashboardDto
import com.kaanf.core.data.dto.EventDetailDto
import com.kaanf.core.data.mappers.toDomain
import com.kaanf.core.data.networking.get
import com.kaanf.core.domain.model.event.EventDashboard
import com.kaanf.core.domain.model.event.EventDetail
import com.kaanf.core.domain.model.event.EventId
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.data.networking.mapCatching
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
            // Tek bozuk event (bilinmeyen status, bozuk tarih) tüm dashboard'ı düşürmesin: atla.
            events.mapNotNull { dto -> runCatching { dto.toDomain() }.getOrNull() }
        }
    }

    override suspend fun getEventDetail(eventId: EventId): Result<EventDetail, DataError.Remote> {
        return httpClient.get<EventDetailDto>(
            route = "/events/$eventId",
        ).mapCatching { it.toDomain() }
    }
}
