package com.kaanf.core.data.networking

object UrlConstants {
    const val BASE_URL_HTTP = "http://10.0.1.10:8096/api"

    // WebSocket kökü: /api yok, endpoint'ler /ws altında.
    // Not: emülatörde host makinen için "ws://10.0.2.2:8096" kullan.
    const val BASE_URL_WS = "ws://10.0.1.10:8096"
}
