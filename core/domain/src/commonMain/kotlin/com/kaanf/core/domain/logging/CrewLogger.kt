package com.kaanf.core.domain.logging

interface CrewLogger {
    fun debug(message: String)

    fun info(message: String)

    fun warn(message: String)

    fun error(
        message: String,
        throwable: Throwable? = null,
    )
}
