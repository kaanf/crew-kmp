package com.kaanf.core.data.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.kaanf.core.domain.logging.CrewLogger

object KermitLogger : CrewLogger {
    override fun debug(message: String) {
        Logger.d(message)
    }

    override fun info(message: String) {
        Logger.i(message)
    }

    override fun warn(message: String) {
        Logger.w(message)
    }

    override fun error(
        message: String,
        throwable: Throwable?,
    ) {
        Logger.e(message, throwable)
    }
}

/**
 * Sürüm binary'sinde debug/info çağrıları writer'a hiç ulaşmasın. Çağrı yerlerindeki string
 * kurulumu kalır ama asıl maliyet (logcat/NSLog yazımı) ve token sızıntısı gider.
 */
fun KermitLogger.applyMinSeverity(isDebug: Boolean): KermitLogger = apply {
    Logger.setMinSeverity(if (isDebug) Severity.Verbose else Severity.Warn)
}
