package com.kaanf.core.data.networking

import com.kaanf.core.data.BuildEnvironment
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.http.Url
import io.ktor.http.encodedPath

/** Dedicated unauthenticated client: production sessions never enter the verification request. */
internal suspend fun verifySentryRequest(
    baseUrl: String,
    buildEnvironment: BuildEnvironment,
    engine: HttpClientEngine? = null,
): String {
    check(buildEnvironment.isDebug) { "Sentry verification requires a debug build" }
    val origin = Url(baseUrl)
    require(origin.protocol.name == "http" && origin.host in setOf("localhost", "127.0.0.1", "10.0.2.2") &&
        origin.user.isNullOrEmpty() && origin.password.isNullOrEmpty() && origin.parameters.isEmpty() &&
        origin.fragment.isEmpty() && origin.encodedPath in setOf("", "/")) {
        "Use a local HTTP origin, for example http://127.0.0.1:18096"
    }
    val configuration: HttpClientConfig<*>.() -> Unit = {
        followRedirects = false
        install(HttpTimeout) { requestTimeoutMillis = 10_000 }
        install(RequestDiagnostics) { apiBaseUrl = "${baseUrl.trimEnd('/')}/api" }
    }
    val client = if (engine == null) HttpClient(configuration) else HttpClient(engine, configuration)
    try {
        val response = client.post("${baseUrl.trimEnd('/')}/api/debug/sentry")
        check(response.status.value == 500) { "Expected the verification server to return HTTP 500" }
        return checkNotNull(response.headers[REQUEST_ID_HEADER]) { "Missing backend request ID" }
    } finally {
        client.close()
    }
}

suspend fun verifyLocalSentry(baseUrl: String, buildEnvironment: BuildEnvironment): String =
    verifySentryRequest(baseUrl, buildEnvironment)
