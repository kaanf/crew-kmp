package com.kaanf.game.data.repository

import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.callContext
import io.ktor.client.engine.HttpClientEngineBase
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.InternalAPI
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MatchRepositoryDateTest {
    @Test
    fun malformedHistoryDatesReturnSerializationInsteadOfThrowing() = runBlocking {
        for (date in listOf("invalid", "", "2026-02-30T00:00:00Z")) {
            val client = client("""[{"matchId":"m","state":"Completed","won":true,"myPoints":1,"opponentPoints":0,"startedAt":"$date"}]""")
            try {
                assertEquals(Result.Failure(DataError.Remote.SERIALIZATION), MatchRepositoryImpl(client).getMatchHistory("e", 0, 20))
            } finally { client.close() }
        }
    }

    @Test
    fun validHistoryDateStillLoads() = runBlocking {
        val client = client("""[{"matchId":"m","state":"Completed","won":true,"myPoints":1,"opponentPoints":0,"startedAt":"2026-10-28T12:00:00Z"}]""")
        try {
            val result = MatchRepositoryImpl(client).getMatchHistory("e", 0, 20)
            assertEquals(1, assertIs<Result.Success<*>>(result).data.let { it as List<*> }.size)
        } finally { client.close() }
    }

    @Test
    fun malformedPassportDateIsHandledForLoadAndClaims() = runBlocking {
        val client = client("""{"totalCount":1,"entries":[{"participantId":"p","userId":"u","fullName":"Fixture","metAt":"invalid"}]}""")
        try {
            val repository = MatchRepositoryImpl(client)
            val failure = Result.Failure(DataError.Remote.SERIALIZATION)
            assertEquals(failure, repository.getAddressBook("e"))
            assertEquals(failure, repository.claimMeeting("e", "u"))
            assertEquals(failure, repository.claimAllMeetings("e"))
        } finally { client.close() }
    }

    private fun client(body: String) = HttpClient(JsonFixtureEngine(body)) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }
}

@OptIn(InternalAPI::class)
private class JsonFixtureEngine(private val responseBody: String) : HttpClientEngineBase("date-fixture") {
    override val config = HttpClientEngineConfig()
    override suspend fun execute(data: HttpRequestData) = HttpResponseData(
        HttpStatusCode.OK, GMTDate(), headersOf(HttpHeaders.ContentType, "application/json"),
        HttpProtocolVersion.HTTP_1_1, ByteReadChannel(responseBody), callContext(),
    )
}
