package com.kaanf.core.data.repository

import com.kaanf.core.data.dto.RefreshRequest
import com.kaanf.core.data.networking.delete
import com.kaanf.core.data.networking.post
import com.kaanf.core.domain.repository.AuthSessionRepository
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import com.kaanf.core.domain.util.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.AuthCircuitBreaker
import io.ktor.client.request.bearerAuth

class AuthSessionRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage,
) : AuthSessionRepository {
    override suspend fun logout() {
        val snapshot = sessionStorage.snapshot()
        val refreshToken = snapshot.authInfo?.refreshToken

        // Oturumu önce yerelde kapatıyoruz: navigasyonu bu tetikliyor ve ağ yanıtını beklemek
        // kullanıcıyı butona bastıktan sonra saniyelerce ekranda bırakabiliyordu.
        sessionStorage.clearSession(snapshot)

        if (!refreshToken.isNullOrBlank()) {
            // Best-effort revoke; the result is intentionally ignored so a network failure still
            // lets the user sign out locally.
            httpClient.post<RefreshRequest, Unit>(
                route = "/auth/logout",
                body = RefreshRequest(refreshToken = refreshToken),
            )
        }
    }

    override suspend fun deleteAccount(): EmptyResult<DataError.Remote> {
        val snapshot = sessionStorage.snapshot()
        val auth = snapshot.authInfo ?: return Result.Failure(DataError.Remote.UNAUTHORIZED)
        // A destructive request must stay bound to the account that confirmed the action.
        // Do not let a later login or an automatic token retry switch its identity.
        return httpClient.delete<Unit>(
            route = "/profile",
            builder = {
                bearerAuth(auth.accessToken)
                attributes.put(AuthCircuitBreaker, Unit)
            },
        ).onSuccess { sessionStorage.clearSession(snapshot) }
    }
}
