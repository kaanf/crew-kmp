package com.kaanf.game.data.network

import platform.Foundation.NSError
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorTimedOut

actual class ConnectionErrorHandler {
    actual fun isRetriableError(cause: Throwable): Boolean {
        if (cause is IOSNetworkCancellationException) {
            return true
        }

        return when (extractNsError(cause)?.code) {
            NSURLErrorNotConnectedToInternet,
            NSURLErrorNetworkConnectionLost,
            NSURLErrorTimedOut -> true
            else -> false
        }
    }

    private fun extractNsError(cause: Throwable): NSError? {
        val throwableCause = cause.cause
        if (throwableCause is NSError) {
            return throwableCause
        }

        if (cause is NSError) {
            return cause
        }

        val exceptionNsError = cause.toNSError()
        val causeNsError = cause.cause?.toNSError()

        return exceptionNsError ?: causeNsError
    }

    private fun Throwable.toNSError(): NSError? {
        return message?.let { message ->
            when {
                message.contains(NSURLErrorNotConnectedToInternetPattern) ->
                    NSError.errorWithDomain(
                        domain = NSURLErrorDomain,
                        code = NSURLErrorNotConnectedToInternet,
                        userInfo = null
                    )
                message.contains(NSURLErrorNetworkConnectionLostPattern) ->
                    NSError.errorWithDomain(
                        domain = NSURLErrorDomain,
                        code = NSURLErrorNetworkConnectionLost,
                        userInfo = null
                    )
                else -> null
            }
        }
    }

    companion object {
        private val NSURLErrorNotConnectedToInternetPattern =
            "Error Domain=${NSURLErrorDomain} Code=${NSURLErrorNotConnectedToInternet}"
        private val NSURLErrorNetworkConnectionLostPattern =
            "Error Domain=${NSURLErrorDomain} Code=${NSURLErrorNetworkConnectionLost}"
    }
}
