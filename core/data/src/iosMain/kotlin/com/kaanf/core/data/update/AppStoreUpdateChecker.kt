package com.kaanf.core.data.update

import com.kaanf.core.domain.update.AppUpdateChecker
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import kotlin.coroutines.cancellation.CancellationException

/**
 * App Store'un public lookup API'si. Uygulamanın ortak HttpClient'ı kullanılmaz:
 * onun bearer plugin'i access token'ı Apple'a da gönderirdi.
 */
class AppStoreUpdateChecker(
    engine: HttpClientEngine,
) : AppUpdateChecker {
    private val client = HttpClient(engine)
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun storeUrlIfOutdated(): String? {
        val bundle = NSBundle.mainBundle
        val bundleId = bundle.bundleIdentifier ?: return null
        val currentVersion = bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
            ?: return null

        val storeApp = try {
            val body = client.get("https://itunes.apple.com/lookup") {
                parameter("bundleId", bundleId)
                // Lookup varsayılan olarak US mağazasına bakar; uygulama orada yoksa boş döner.
                parameter("country", NSLocale.currentLocale.countryCode ?: "us")
            }.bodyAsText()
            json.decodeFromString<LookupResponse>(body).results.firstOrNull()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        } ?: return null

        // Eşitlik yerine sıralama: review/TestFlight build'i mağazadakinden yeni olabilir.
        return storeApp.trackViewUrl.takeIf { storeApp.version.isNewerVersionThan(currentVersion) }
    }

    @Serializable
    private data class LookupResponse(val results: List<StoreApp> = emptyList())

    @Serializable
    private data class StoreApp(val version: String, val trackViewUrl: String)
}
