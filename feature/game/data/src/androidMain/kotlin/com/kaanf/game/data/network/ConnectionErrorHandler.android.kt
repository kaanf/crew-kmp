package com.kaanf.game.data.network

import io.ktor.client.plugins.websocket.WebSocketException
import io.ktor.network.sockets.SocketTimeoutException
import kotlinx.io.EOFException
import java.net.SocketException
import java.net.UnknownHostException

actual class ConnectionErrorHandler {
    actual fun isRetriableError(cause: Throwable): Boolean {
        return when (cause) {
            is SocketTimeoutException,
            is WebSocketException,
            is SocketException,
            is UnknownHostException,
            is EOFException -> true
            else -> false
        }
    }
}
