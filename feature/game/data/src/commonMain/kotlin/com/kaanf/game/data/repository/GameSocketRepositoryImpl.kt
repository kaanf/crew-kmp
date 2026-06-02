package com.kaanf.game.data.repository

import com.kaanf.core.data.networking.UrlConstants
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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class GameSocketRepositoryImpl(
    private val httpClient: HttpClient,
) : GameSocketRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun observeEvents(eventId: String): Flow<GameSocketMessage> = flow {
        // Handshake'e Authorization header'ı Auth plugin'i otomatik ekler.
        val session = httpClient.webSocketSession {
            url("${UrlConstants.BASE_URL_WS}/ws/events")
            parameter("eventId", eventId)
        }

        try {
            // Kanal kapanana ya da scope iptal edilene kadar mesajları dinle.
            for (frame in session.incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    val type = runCatching {
                        json.parseToJsonElement(text)
                            .jsonObject["type"]
                            ?.jsonPrimitive
                            ?.content
                    }.getOrNull().orEmpty()

                    emit(GameSocketMessage(type = type, raw = text))
                }
            }
        } finally {
            session.close()
        }
    }
}
