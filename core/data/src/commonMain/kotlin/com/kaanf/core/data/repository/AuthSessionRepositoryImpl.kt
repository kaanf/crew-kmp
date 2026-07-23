package com.kaanf.core.data.repository

import com.kaanf.core.data.dto.RefreshRequest
import com.kaanf.core.data.networking.delete
import com.kaanf.core.data.networking.post
import com.kaanf.core.domain.repository.AuthSessionRepository
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.onSuccess
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.firstOrNull

class AuthSessionRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage,
) : AuthSessionRepository {
    override suspend fun logout() {
        val refreshToken = sessionStorage.observeAuthInfo().firstOrNull()?.refreshToken

        if (!refreshToken.isNullOrBlank()) {
            // Best-effort revoke; the result is intentionally ignored so a network failure still
            // lets the user sign out locally.
            httpClient.post<RefreshRequest, Unit>(
                route = "/auth/logout",
                body = RefreshRequest(refreshToken = refreshToken),
            )
        }

        sessionStorage.set(null)
    }

    override suspend fun deleteAccount(): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(route = "/profile")
            .onSuccess { sessionStorage.set(null) }
    }
}
