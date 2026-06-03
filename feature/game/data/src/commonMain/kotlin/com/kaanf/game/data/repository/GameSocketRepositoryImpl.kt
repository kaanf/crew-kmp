package com.kaanf.game.data.repository

import com.kaanf.core.data.networking.UrlConstants
import com.kaanf.game.data.dto.SocketEnvelopeDto
import com.kaanf.game.data.mappers.toDomain
import com.kaanf.game.domain.model.GameSocketMessage
import com.kaanf.game.domain.repository.GameSocketRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class GameSocketRepositoryImpl(
    private val httpClient: HttpClient,
) : GameSocketRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun observeEvents(eventId: String): Flow<GameSocketMessage> = flow {
        val session = httpClient.webSocketSession {
            url("${UrlConstants.BASE_URL_WS}/ws/events")
            parameter("eventId", eventId)
        }

        try {
            for (frame in session.incoming) {
                if (frame !is Frame.Text) continue

                val envelope = runCatching {
                    json.decodeFromString<SocketEnvelopeDto>(frame.readText())
                }.getOrNull() ?: continue

                emit(envelope.toDomain(json))
            }
        } finally {
            session.close()
        }
    }
}
