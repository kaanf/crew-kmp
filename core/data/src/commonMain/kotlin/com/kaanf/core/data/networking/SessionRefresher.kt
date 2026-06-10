package com.kaanf.core.data.networking

import com.kaanf.core.data.dto.AuthInfoSerializable
import com.kaanf.core.data.dto.RefreshRequest
import com.kaanf.core.data.mappers.toDomain
import com.kaanf.core.domain.model.auth.AuthInfo
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.AuthCircuitBreaker
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SessionRefresher(
    private val sessionStorage: SessionStorage,
) {
    private val mutex = Mutex()

    suspend fun refresh(client: HttpClient): AuthInfo? = mutex.withLock {
        val refreshToken = sessionStorage.observeAuthInfo().firstOrNull()?.refreshToken
        if (refreshToken.isNullOrBlank()) {
            sessionStorage.set(null)
            return@withLock null
        }

        var refreshed: AuthInfo? = null
        client.post<RefreshRequest, AuthInfoSerializable>(
            route = "/auth/refresh",
            body = RefreshRequest(refreshToken = refreshToken),
            builder = { attributes.put(AuthCircuitBreaker, Unit) },
        ).onSuccess { newAuthInfo ->
            val domain = newAuthInfo.toDomain()
            sessionStorage.set(domain)
            refreshed = domain
        }.onFailure { error ->
            if (error == DataError.Remote.UNAUTHORIZED || error == DataError.Remote.FORBIDDEN) {
                sessionStorage.set(null)
            }
        }

        refreshed
    }
}
