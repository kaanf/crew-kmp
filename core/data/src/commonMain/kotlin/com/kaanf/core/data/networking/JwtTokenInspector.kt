package com.kaanf.core.data.networking

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * JWT'nin `exp` claim'ini imza doğrulamadan okur — doğrulama backend'in işi. Tek amacı
 * proaktif token-yenileme zamanlaması: el sıkışmadan önce bayat access token'ı yenileyip
 * doomed bir WS handshake'ini (ve iOS/Darwin'de güvenilir okunamayan handshake 401'ini)
 * baştan engellemek.
 */
@OptIn(ExperimentalEncodingApi::class)
object JwtTokenInspector {
    private val json = Json { ignoreUnknownKeys = true }

    /** JWT payload'ındaki `exp` (epoch saniye). Token JWT değilse/decode edilemezse null. */
    fun expiresAtEpochSeconds(token: String): Long? {
        val rawToken = token.removePrefix("Bearer ")
        val parts = rawToken.split(".")
        if (parts.size < 2) return null
        return try {
            val payload = Base64.UrlSafe
                .withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)
                .decode(parts[1])
                .decodeToString()
            json.parseToJsonElement(payload).jsonObject["exp"]?.jsonPrimitive?.longOrNull
        } catch (e: Exception) {
            null
        }
    }
}
