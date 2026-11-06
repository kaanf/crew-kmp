package com.kaanf.core.data

import com.kaanf.core.data.repository.MusicRepositoryImpl
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineBase
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.callContext
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.*

class MusicRepositoryTest {
    private val engine = MusicEngine()
    private val client = HttpClient(engine) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        defaultRequest { contentType(ContentType.Application.Json) }
    }
    private val repository = MusicRepositoryImpl(client)
    private val id = "0000000000000000000001"
    private val track = """{"id":"$id","title":"Fixture Song","artists":["Fixture Band"],"spotifyUrl":"https://open.spotify.com/track/$id"}"""

    @AfterTest fun close() = client.close()

    @Test
    fun searchEncodesQueryAndMapsPagination() = runBlocking {
        engine.response = """{"tracks":[$track],"nextOffset":10,"unknown":"ignored"}"""
        val result = repository.search("  band & song  ", "tr") as Result.Success
        assertEquals("Fixture Band", result.data.tracks.single().artists.single())
        assertEquals(10, result.data.nextOffset)
        assertEquals("band & song", engine.requests.single().url.parameters["q"])
        assertEquals("TR", engine.requests.single().url.parameters["market"])
        assertEquals("0", engine.requests.single().url.parameters["offset"])
    }

    @Test
    fun invalidInputsDoNotMakeNetworkCalls() = runBlocking {
        assertIs<Result.Failure<*>>(repository.search("a", "TR"))
        assertIs<Result.Failure<*>>(repository.search("ab\ncd", "TR"))
        assertIs<Result.Failure<*>>(repository.search("song", "TR", 1001))
        assertIs<Result.Failure<*>>(repository.search("song", "TUR"))
        assertIs<Result.Failure<*>>(repository.saveFavorite("untrusted", "TR"))
        assertTrue(engine.requests.isEmpty())
    }

    @Test
    fun missingFavoriteIsNullAndSaveRequiresTrackInResponse() = runBlocking {
        engine.response = """{"track":null}"""
        assertNull((repository.getFavorite("TR") as Result.Success).data)
        assertEquals(DataError.Remote.SERIALIZATION,
            (repository.saveFavorite(id, "TR") as Result.Failure).error)
    }

    @Test
    fun saveAndClearUseProfileEndpoints() = runBlocking {
        engine.response = """{"track":$track}"""
        assertEquals(id, (repository.saveFavorite(id, "TR") as Result.Success).data.id)
        assertEquals(HttpMethod.Put, engine.requests.last().method)
        assertTrue(engine.requests.last().url.encodedPath.endsWith("/profile/favorite-music"))
        engine.status = HttpStatusCode.NoContent
        engine.response = ""
        assertIs<Result.Success<Unit>>(repository.clearFavorite())
        assertEquals(HttpMethod.Delete, engine.requests.last().method)
    }

    @Test
    fun rateLimitAndUnavailableAreFailuresNotEmptyResults() = runBlocking {
        engine.status = HttpStatusCode.TooManyRequests
        engine.response = """{"code":"MUSIC_RATE_LIMITED","message":"Retry later"}"""
        val failure = repository.search("song", "TR") as Result.Failure
        assertEquals("MUSIC_RATE_LIMITED", (failure.error as DataError.Remote.Business).code)
        engine.status = HttpStatusCode.ServiceUnavailable
        assertEquals(DataError.Remote.SERVICE_UNAVAILABLE,
            (repository.getFavorite("TR") as Result.Failure).error)
    }

    @Test
    fun malformedTrackLinksAreRejected() = runBlocking {
        engine.response = """{"track":${track.replace("https://open.spotify.com/track/$id", "https://untrusted.invalid")}}"""
        assertEquals(DataError.Remote.SERIALIZATION,
            (repository.getFavorite("TR") as Result.Failure).error)
    }

    @Test
    fun cancellationIsPreserved() = runBlocking {
        engine.cancel = true
        assertFailsWith<CancellationException> { repository.search("song", "TR") }
        Unit
    }
}

@OptIn(InternalAPI::class)
private class MusicEngine : HttpClientEngineBase("music-fixture") {
    override val config = HttpClientEngineConfig()
    var response = "{}"
    var status = HttpStatusCode.OK
    var cancel = false
    val requests = mutableListOf<HttpRequestData>()
    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        requests += data
        if (cancel) throw CancellationException("Fixture cancellation")
        return HttpResponseData(status, GMTDate(), headersOf(HttpHeaders.ContentType, "application/json"),
            HttpProtocolVersion.HTTP_1_1, ByteReadChannel(response), callContext())
    }
}
