package com.kaanf.core.data

import com.kaanf.core.data.repository.MovieRepositoryImpl
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

class MovieRepositoryTest {
    private val engine = MovieEngine()
    private val client = HttpClient(engine) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        defaultRequest { contentType(ContentType.Application.Json) }
    }
    private val repository = MovieRepositoryImpl(client)
    private val id = 123L
    private val movie = """{"id":$id,"title":"Fixture Film","releaseYear":"2020","posterUrl":"https://image.tmdb.org/t/p/w342/fixture.jpg","tmdbUrl":"https://www.themoviedb.org/movie/$id"}"""

    @AfterTest fun close() = client.close()

    @Test
    fun searchEncodesQueryAndMapsPagination() = runBlocking {
        engine.response = """{"movies":[$movie],"nextPage":2,"unknown":"ignored"}"""
        val result = repository.search("  film & sequel  ", "tr") as Result.Success
        assertEquals("2020", result.data.movies.single().releaseYear)
        assertEquals(2, result.data.nextPage)
        assertEquals("film & sequel", engine.requests.single().url.parameters["q"])
        assertEquals("tr", engine.requests.single().url.parameters["language"])
        assertEquals("1", engine.requests.single().url.parameters["page"])
    }

    @Test
    fun invalidInputsDoNotMakeNetworkCalls() = runBlocking {
        assertIs<Result.Failure<*>>(repository.search("a", "tr"))
        assertIs<Result.Failure<*>>(repository.search("ab\ncd", "tr"))
        assertIs<Result.Failure<*>>(repository.search("film", "tr", 501))
        assertIs<Result.Failure<*>>(repository.search("film", "invalid"))
        assertIs<Result.Failure<*>>(repository.saveFavorite(-1L, "tr"))
        assertTrue(engine.requests.isEmpty())
    }

    @Test
    fun missingFavoriteIsNullAndSaveRequiresMovieInResponse() = runBlocking {
        engine.response = """{"movie":null}"""
        assertNull((repository.getFavorite("tr") as Result.Success).data)
        assertEquals(DataError.Remote.SERIALIZATION,
            (repository.saveFavorite(id, "tr") as Result.Failure).error)
    }

    @Test
    fun saveAndClearUseProfileEndpoints() = runBlocking {
        engine.response = """{"movie":$movie}"""
        assertEquals(id, (repository.saveFavorite(id, "tr") as Result.Success).data.id)
        assertEquals(HttpMethod.Put, engine.requests.last().method)
        assertTrue(engine.requests.last().url.encodedPath.endsWith("/profile/favorite-movie"))
        engine.status = HttpStatusCode.NoContent
        engine.response = ""
        assertIs<Result.Success<Unit>>(repository.clearFavorite())
        assertEquals(HttpMethod.Delete, engine.requests.last().method)
    }

    @Test
    fun rateLimitAndUnavailableAreFailuresNotEmptyResults() = runBlocking {
        engine.status = HttpStatusCode.TooManyRequests
        engine.response = """{"code":"MOVIE_RATE_LIMITED","message":"Retry later"}"""
        val failure = repository.search("film", "tr") as Result.Failure
        assertEquals("MOVIE_RATE_LIMITED", (failure.error as DataError.Remote.Business).code)
        engine.status = HttpStatusCode.ServiceUnavailable
        assertEquals(DataError.Remote.SERVICE_UNAVAILABLE,
            (repository.getFavorite("tr") as Result.Failure).error)
    }

    @Test
    fun malformedMovieLinksAreRejected() = runBlocking {
        engine.response = """{"movie":${movie.replace("https://www.themoviedb.org/movie/$id", "https://untrusted.invalid")}}"""
        assertEquals(DataError.Remote.SERIALIZATION,
            (repository.getFavorite("tr") as Result.Failure).error)
    }

    @Test
    fun nonAdvancingPagesAndExternalPosterHostsAreRejected() = runBlocking {
        engine.response = """{"movies":[$movie],"nextPage":1}"""
        assertEquals(DataError.Remote.SERIALIZATION,
            (repository.search("film", "en") as Result.Failure).error)
        engine.response = """{"movie":${movie.replace("https://image.tmdb.org/t/p/w342/fixture.jpg", "https://untrusted.invalid/poster.jpg")}}"""
        assertEquals(DataError.Remote.SERIALIZATION,
            (repository.getFavorite("en") as Result.Failure).error)
    }

    @Test
    fun cancellationIsPreserved() = runBlocking {
        engine.cancel = true
        assertFailsWith<CancellationException> { repository.search("film", "tr") }
        Unit
    }
}

@OptIn(InternalAPI::class)
private class MovieEngine : HttpClientEngineBase("movie-fixture") {
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
