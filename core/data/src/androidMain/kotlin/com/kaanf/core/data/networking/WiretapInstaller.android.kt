package com.kaanf.core.data.networking

import dev.skymansandy.wiretap.plugin.http.WiretapKtorHttpPlugin
import dev.skymansandy.wiretap.plugin.ws.WiretapKtorWebSocketPlugin
import io.ktor.client.HttpClientConfig

actual fun HttpClientConfig<*>.installWiretapHttp() {
    install(WiretapKtorHttpPlugin)
}

actual fun HttpClientConfig<*>.installWiretapWebSocket() {
    install(WiretapKtorWebSocketPlugin)
}
