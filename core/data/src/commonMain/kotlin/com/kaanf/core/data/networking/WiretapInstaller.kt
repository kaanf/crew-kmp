package com.kaanf.core.data.networking

import io.ktor.client.HttpClientConfig

/**
 * Installs the Wiretap HTTP plugin on the client. No-op on iOS: Wiretap's iOS klibs are built
 * with the Kotlin 2.3 ABI, which the project's 2.2 compiler cannot consume, so it is Android-only.
 */
expect fun HttpClientConfig<*>.installWiretapHttp()

/**
 * Installs the Wiretap WebSocket plugin. Must be installed after the WebSockets plugin.
 * No-op on iOS (see [installWiretapHttp]).
 */
expect fun HttpClientConfig<*>.installWiretapWebSocket()
