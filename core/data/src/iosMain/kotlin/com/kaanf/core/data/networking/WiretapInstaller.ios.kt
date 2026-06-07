package com.kaanf.core.data.networking

import io.ktor.client.HttpClientConfig

// Wiretap is unavailable on iOS (Kotlin 2.3 ABI mismatch); these are no-ops.
actual fun HttpClientConfig<*>.installWiretapHttp() = Unit

actual fun HttpClientConfig<*>.installWiretapWebSocket() = Unit
