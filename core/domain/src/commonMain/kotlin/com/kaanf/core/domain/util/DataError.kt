@file:Suppress("ClassName", "ClassNaming", "ktlint:standard:class-naming")

package com.kaanf.core.domain.util

sealed interface DataError : Error {
    /**
     * Uzak hatalar. Sabitler HTTP status'tan türer; [Business] ise backend'in {code, message}
     * iş-kuralı yanıtını (400/403/404/409/429) taşır. Eski enum değerleri isim churn'ü olmasın
     * diye SCREAMING_CASE data object olarak korundu (referanslar ve == kıyasları aynı çalışır).
     */
    sealed interface Remote : DataError {
        data object BAD_REQUEST : Remote
        data object REQUEST_TIMEOUT : Remote
        data object UNAUTHORIZED : Remote
        data object FORBIDDEN : Remote
        data object NOT_FOUND : Remote
        data object CONFLICT : Remote
        data object TOO_MANY_REQUESTS : Remote
        data object NO_INTERNET : Remote
        data object PAYLOAD_TOO_LARGE : Remote
        data object SERVER_ERROR : Remote
        data object SERVICE_UNAVAILABLE : Remote
        data object SERIALIZATION : Remote
        data object UNKNOWN : Remote

        /** Backend'in {code, message} iş-kuralı hatası; [code] üzerinden lokalize mesaja çevrilir. */
        data class Business(val code: String, val message: String) : Remote
    }

    enum class Local : DataError {
        DISK_FULL,
        NOT_FOUND,
        UNKNOWN,
    }
}
