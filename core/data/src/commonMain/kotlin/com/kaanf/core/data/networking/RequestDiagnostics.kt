package com.kaanf.core.data.networking

import io.ktor.client.plugins.api.SendingRequest
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.request
import io.ktor.http.encodedPath
import kotlin.uuid.Uuid

/** Installed by the native hosts before networking starts. Contains no URLs or user data. */
object HttpDiagnostics {
    var onResponse: ((requestId: String, operation: String, status: Int) -> Unit)? = null
}

internal const val REQUEST_ID_HEADER = "X-Request-ID"
private val requestIdPattern = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")
private val apiHost = io.ktor.http.Url(UrlConstants.BASE_URL_HTTP).host

@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
internal val RequestDiagnostics = createClientPlugin("CrewRequestDiagnostics") {
    on(SendingRequest) { request, _ ->
        request.headers.remove(REQUEST_ID_HEADER)
        if (request.url.host == apiHost && request.url.protocol.name == "https") {
            request.headers.append(REQUEST_ID_HEADER, Uuid.random().toString())
        }
    }
    onResponse { response ->
        val request = response.request
        if (request.url.host == apiHost && request.url.protocol.name == "https") {
            val id = response.headers[REQUEST_ID_HEADER]?.takeIf { requestIdPattern.matches(it) }
                ?: request.headers[REQUEST_ID_HEADER]
            if (id != null) {
                // Allowlist resource categories; never send path IDs, QR tokens or query strings.
                val resource = request.url.encodedPath.removePrefix("/api/").substringBefore('/')
                val category = resource.takeIf { it in setOf("auth", "events", "users", "tickets", "profile", "me") } ?: "other"
                try {
                    HttpDiagnostics.onResponse?.invoke(id, "${request.method.value} $category", response.status.value)
                } catch (error: kotlinx.coroutines.CancellationException) {
                    throw error
                } catch (_: Exception) {
                    // Diagnostics must never turn a successful API call into a failure.
                }
            }
        }
    }
}
