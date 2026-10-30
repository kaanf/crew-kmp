package com.kaanf.core.data.networking

import com.kaanf.core.data.BuildEnvironment
import com.kaanf.core.domain.logging.CrewLogger
import com.kaanf.core.domain.model.auth.AuthInfo
import com.kaanf.core.domain.repository.SessionSnapshot
import com.kaanf.core.domain.repository.SessionStorage
import io.ktor.client.call.body
import io.ktor.client.engine.callContext
import io.ktor.client.engine.HttpClientEngineBase
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.HttpTimeoutCapability
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.InternalAPI
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HttpClientLoggingTest {
    @Test
    fun debugDoesNotLogBodiesCredentialsOrSignedUrls() = verifyLogging(debug = true)

    @Test
    fun releaseDoesNotLogRequests() = verifyLogging(debug = false)

    private fun verifyLogging(debug: Boolean) = runBlocking {
        val logger = RecordingLogger()
        val session = EmptySessionStorage()
        val engine = LoggingFixtureEngine()
        val client = HttpClientFactory(logger, session, SessionRefresher(session), BuildEnvironment(debug)).create(engine)
        try {
            for (url in listOf(
                "https://fixture.invalid/api/auth/login",
                "https://fixture.invalid/api/auth/refresh",
                "https://fixture.invalid/api/profile",
                "https://fixture.invalid/upload?signature=fixture-signed-secret",
            )) {
                client.post(url) {
                    header(HttpHeaders.Authorization, "Bearer fixture-bearer-secret")
                    header(HttpHeaders.Cookie, "session=fixture-cookie-secret")
                    setBody("""{"password":"fixture-password-secret","token":"fixture-request-secret"}""")
                }.body<String>()
            }
        } finally {
            client.close()
            engine.close()
        }
        val output = logger.messages.value.joinToString("\n")
        for (secret in listOf("fixture-bearer-secret", "fixture-cookie-secret", "fixture-password-secret", "fixture-request-secret", "fixture-response-secret", "fixture-signed-secret", "fixture-response-cookie")) {
            assertFalse(output.contains(secret), "Credential must never enter logger")
        }
        assertFalse(output.contains("/auth/"))
        assertFalse(output.contains("/upload"))
        if (debug) assertTrue(output.contains("/api/profile"), "Test must exercise enabled debug logging")
        else assertTrue(output.isEmpty())
    }
}

private class RecordingLogger : CrewLogger {
    val messages = MutableStateFlow(emptyList<String>())
    override fun debug(message: String) { messages.update { it + message } }
    override fun info(message: String) = Unit
    override fun warn(message: String) = Unit
    override fun error(message: String, throwable: Throwable?) = Unit
}

private class EmptySessionStorage : SessionStorage {
    private val auth = MutableStateFlow<AuthInfo?>(null)
    override fun observeAuthInfo() = auth
    override suspend fun set(info: AuthInfo?) = Unit
    override suspend fun update(transform: (AuthInfo?) -> AuthInfo?) = Unit
    override suspend fun snapshot() = SessionSnapshot(null, 0)
    override suspend fun clearSession(expected: SessionSnapshot) = false
    override suspend fun compareAndSet(expected: SessionSnapshot, info: AuthInfo?) = false
}

@OptIn(InternalAPI::class)
private class LoggingFixtureEngine : HttpClientEngineBase("logging-fixture") {
    override val config = HttpClientEngineConfig()
    override val supportedCapabilities = setOf(HttpTimeoutCapability)
    override suspend fun execute(data: HttpRequestData) = HttpResponseData(
        HttpStatusCode.OK, GMTDate(),
        headersOf(HttpHeaders.ContentType to listOf("application/json"), HttpHeaders.SetCookie to listOf("session=fixture-response-cookie")),
        HttpProtocolVersion.HTTP_1_1, ByteReadChannel("""{"token":"fixture-response-secret"}"""), callContext(),
    )
}
