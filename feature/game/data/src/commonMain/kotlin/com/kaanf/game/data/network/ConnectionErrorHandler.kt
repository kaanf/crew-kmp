package com.kaanf.game.data.network

expect class ConnectionErrorHandler {
    fun isRetriableError(cause: Throwable): Boolean
}
