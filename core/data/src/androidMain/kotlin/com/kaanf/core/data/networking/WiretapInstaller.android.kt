package com.kaanf.core.data.networking

// import dev.skymansandy.wiretap.plugin.http.WiretapKtorHttpPlugin
// import dev.skymansandy.wiretap.plugin.ws.WiretapKtorWebSocketPlugin
import io.ktor.client.HttpClientConfig

// Wiretap devre dışı (bağımlılıklar yorumda). Geri açmak için import + install satırlarını aç.
actual fun HttpClientConfig<*>.installWiretapHttp() {
//    install(WiretapKtorHttpPlugin)
}

actual fun HttpClientConfig<*>.installWiretapWebSocket() {
//    install(WiretapKtorWebSocketPlugin)
}
