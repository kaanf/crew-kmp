package com.kaanf.core.data.networking

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.kaanf.core.data.mappers.toSerializable
import com.kaanf.core.data.BuildEnvironment
import com.kaanf.core.domain.logging.CrewLogger
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.data.repository.AuthSessionRepositoryImpl
import com.kaanf.core.data.session.DataStoreSessionStorage
import com.kaanf.core.data.session.SecureSessionStore
import com.kaanf.core.domain.model.auth.AuthInfo
import com.kaanf.core.domain.model.user.User
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineBase
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.callContext
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.HttpTimeoutCapability
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionRefreshFlowTest {
    @Test
    fun deleteAccountSendsCapturedBearerAndClearsSessionOnSuccess() = verifyAccountDeletion(HttpStatusCode.NoContent)

    @Test
    fun unauthorizedDeleteKeepsSessionAndDoesNotRefreshOrRetry() = verifyAccountDeletion(HttpStatusCode.Unauthorized)

    private fun verifyAccountDeletion(status: HttpStatusCode) = runBlocking {
        val storage = storage()
        val original = auth()
        storage.set(original)
        val requests = mutableListOf<HttpRequestData>()
        val engine = AccountDeleteEngine(status, requests)
        val logger = object : CrewLogger {
            override fun debug(message: String) = Unit
            override fun info(message: String) = Unit
            override fun warn(message: String) = Unit
            override fun error(message: String, throwable: Throwable?) = Unit
        }
        val client = HttpClientFactory(logger, storage, SessionRefresher(storage), BuildEnvironment(false)).create(engine)
        try {
            // Exercise normal automatic bearer auth before the explicitly authenticated DELETE.
            client.get<Unit>("/profile")
            val result = AuthSessionRepositoryImpl(client, storage).deleteAccount()
            assertEquals(2, requests.size, "DELETE must not trigger a refresh or retry")
            assertEquals(HttpMethod.Get, requests[0].method)
            assertEquals(listOf("Bearer ${original.accessToken}"), requests[0].headers.getAll(HttpHeaders.Authorization))
            assertEquals(HttpMethod.Delete, requests[1].method)
            assertEquals("/api/profile", requests[1].url.encodedPath)
            assertEquals(listOf("Bearer ${original.accessToken}"), requests[1].headers.getAll(HttpHeaders.Authorization))
            if (status == HttpStatusCode.NoContent) {
                assertEquals(Result.Success(Unit), result)
                assertNull(storage.snapshot().authInfo)
            } else {
                assertEquals(Result.Failure(DataError.Remote.UNAUTHORIZED), result)
                assertEquals(original, storage.snapshot().authInfo)
            }
        } finally {
            client.close()
            engine.close()
        }
    }

    @Test
    fun successfulHttpRefreshArrivingAfterLogoutCannotRestoreTheSession() = runBlocking {
        withTimeout(10_000) {
            val storage = storage()
            storage.set(auth())
            val engine = DelayedRefreshEngine(Json.encodeToString(auth("rotated").toSerializable()), HttpStatusCode.OK)
            val client = HttpClient(engine) {
                install(ContentNegotiation) { json() }
                defaultRequest { contentType(ContentType.Application.Json) }
            }
            try {
                val refresh = async { SessionRefresher(storage).refresh(client) }
                engine.started.await()
                AuthSessionRepositoryImpl(client, storage).logout()
                engine.release.complete(Unit)
                assertNull(refresh.await())
                assertNull(storage.snapshot().authInfo)
            } finally {
                engine.release.complete(Unit)
                client.close()
                engine.close()
            }
        }
    }

    @Test
    fun failedHttpRefreshCannotClearAnIdenticalButNewLogin() = runBlocking {
        withTimeout(10_000) {
            val storage = storage()
            storage.set(auth())
            val engine = DelayedRefreshEngine("{}", HttpStatusCode.Unauthorized)
            val client = HttpClient(engine) {
                install(ContentNegotiation) { json() }
                defaultRequest { contentType(ContentType.Application.Json) }
            }
            try {
                val refresh = async { SessionRefresher(storage).refresh(client) }
                engine.started.await()
                AuthSessionRepositoryImpl(client, storage).logout()
                storage.set(auth())
                engine.release.complete(Unit)
                assertNull(refresh.await())
                assertEquals(auth(), storage.snapshot().authInfo)
            } finally {
                engine.release.complete(Unit)
                client.close()
                engine.close()
            }
        }
    }

    private fun auth(suffix: String = "original") = AuthInfo(
        "fixture-access-$suffix", "fixture-refresh-$suffix",
        User("fixture-user", "fixture@example.invalid", "Fixture", role = "User", status = "Active", hasVerifiedEmail = true, isProfileComplete = true),
    )

    private fun storage(): DataStoreSessionStorage {
        val preferences = object : DataStore<Preferences> {
            override val data = MutableStateFlow(emptyPreferences())
            override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
                data.value = transform(data.value)
                return data.value
            }
        }
        val protected = object : SecureSessionStore {
            private var value: String? = null
            override suspend fun read() = value
            override suspend fun write(value: String) { this.value = value }
        }
        return DataStoreSessionStorage(preferences, protected, onLegacyCleared = {}) {}
    }
}

@OptIn(InternalAPI::class)
private class AccountDeleteEngine(
    private val deleteStatus: HttpStatusCode,
    private val requests: MutableList<HttpRequestData>,
) : HttpClientEngineBase("account-delete-fixture") {
    override val config = HttpClientEngineConfig()
    override val supportedCapabilities = setOf(HttpTimeoutCapability)
    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        requests.add(data)
        return HttpResponseData(
            if (data.method == HttpMethod.Delete) deleteStatus else HttpStatusCode.NoContent,
            GMTDate(), headersOf(), HttpProtocolVersion.HTTP_1_1, ByteReadChannel(""), callContext(),
        )
    }
}

@OptIn(InternalAPI::class)
private class DelayedRefreshEngine(private val response: String, private val status: HttpStatusCode) : HttpClientEngineBase("session-race-fixture") {
    override val config = HttpClientEngineConfig()
    val started = CompletableDeferred<Unit>()
    val release = CompletableDeferred<Unit>()

    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        val refresh = data.url.encodedPath.endsWith("/refresh")
        if (refresh) {
            started.complete(Unit)
            release.await()
        }
        return HttpResponseData(
            if (refresh) status else HttpStatusCode.NoContent, GMTDate(),
            headersOf(HttpHeaders.ContentType, "application/json"), HttpProtocolVersion.HTTP_1_1,
            ByteReadChannel(if (refresh) response else ""), callContext(),
        )
    }
}
