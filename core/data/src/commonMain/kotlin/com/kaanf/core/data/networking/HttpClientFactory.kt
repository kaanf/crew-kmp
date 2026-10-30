package com.kaanf.core.data.networking

import com.kaanf.core.data.BuildEnvironment
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.logging.CrewLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.AuthCircuitBreaker
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val crewLogger: CrewLogger,
    private val sessionStorage: SessionStorage,
    private val sessionRefresher: SessionRefresher,
    private val buildEnvironment: BuildEnvironment,
) {
    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
//            installWiretapHttp()
            install(ContentNegotiation) {
                json(
                    json =
                        Json {
                            ignoreUnknownKeys = true
                        },
                )
            }
            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
            }
            install(Logging) {
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            crewLogger.debug(message)
                        }
                    }
                // Never log bodies (passwords, tokens, user data) even in debug builds.
                level = if (buildEnvironment.isDebug) LogLevel.INFO else LogLevel.NONE
                sanitizeHeader {
                    it.equals(HttpHeaders.Authorization, ignoreCase = true) ||
                        it.equals(HttpHeaders.Cookie, ignoreCase = true) ||
                        it.equals(HttpHeaders.SetCookie, ignoreCase = true)
                }
                filter { request ->
                    // INFO includes the URL: omit auth and signed/query-bearing URLs entirely.
                    !request.url.encodedPath.startsWith("/auth/") &&
                        !request.url.encodedPath.contains("/auth/") &&
                        request.url.parameters.isEmpty()
                }
            }
            // Keepalive otoritesi backend'de (server PING/PONG + pong-timeout). Client kendi
            // ping'ini atmaz; Ktor'un DefaultWebSocketSession ponger'ı gelen server PING'lerine
            // otomatik PONG döner (ponger pingInterval'dan bağımsız hep aktiftir).
            install(WebSockets)
            // WebSocket trafiğini Wiretap konsoluna yansıtır (WebSockets'ten sonra kurulmalı).
//            installWiretapWebSocket()
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
            install(Auth) {
                bearer {
                    // These requests supply their own credentials and disable automatic retries.
                    // Ktor 3.3.3 otherwise removes their Authorization header before sending.
                    sendWithoutRequest { request -> !request.attributes.contains(AuthCircuitBreaker) }
                    loadTokens {
                        sessionStorage
                            .observeAuthInfo()
                            .firstOrNull()
                            ?.let {
                                BearerTokens(
                                    accessToken = it.accessToken,
                                    refreshToken = it.refreshToken
                                )
                            }
                    }

                    refreshTokens {
                        if (response.request.url.encodedPath.contains("auth/")) {
                            return@refreshTokens null
                        }

                        // Yenilemenin kendisi (storage güncelleme, 401/403'te logout dahil)
                        // SessionRefresher'da: event soketiyle ortak tek kaynak.
                        sessionRefresher.refresh(client)?.let { authInfo ->
                            BearerTokens(
                                accessToken = authInfo.accessToken,
                                refreshToken = authInfo.refreshToken,
                            )
                        }
                    }
                }
            }
        }
    }
}
