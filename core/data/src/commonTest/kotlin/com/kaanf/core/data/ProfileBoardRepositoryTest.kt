package com.kaanf.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.kaanf.core.data.repository.ProfileBoardRepositoryImpl
import com.kaanf.core.domain.model.profileboard.BoardQrRenderStatus
import com.kaanf.core.domain.model.profileboard.ProfileBoard
import com.kaanf.core.domain.model.user.User
import com.kaanf.core.domain.repository.UserStore
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineBase
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.callContext
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.*

class ProfileBoardRepositoryTest {
    private val currentUser = MutableStateFlow<User?>(user("first"))
    private val users = object : UserStore {
        override fun observeCurrentUser() = currentUser
        override suspend fun updateCurrentUser(user: User) { currentUser.value = user }
    }
    private val preferences = object : DataStore<Preferences> {
        override val data = MutableStateFlow(emptyPreferences())
        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
            data.value = transform(data.value)
            return data.value
        }
    }
    private val engine = BoardEngine()
    private val client = HttpClient(engine) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        defaultRequest { contentType(ContentType.Application.Json) }
    }
    private val repository = ProfileBoardRepositoryImpl(client, users, preferences)

    @AfterTest fun closeClient() { client.close() }

    @Test
    fun cachedBoardsStayWithTheirAccount() = runBlocking {
        engine.response = """{"revision":3,"qrStyleId":"classic"}"""
        repository.refresh()
        assertEquals("classic", repository.observeBoard().first()?.qrStyleId)
        currentUser.value = user("second")
        assertNull(repository.observeBoard().first())
        engine.response = """{"revision":1,"qrStyleId":"dots"}"""
        repository.refresh()
        assertEquals("dots", repository.observeBoard().first()?.qrStyleId)
        currentUser.value = user("first")
        assertEquals("classic", repository.observeBoard().first()?.qrStyleId)
        currentUser.value = null
        assertNull(repository.observeBoard().first())
    }

    @Test
    fun delayedOlderSnapshotCannotReplaceNewerCachedRevision() = runBlocking {
        engine.response = """{"revision":5,"qrStyleId":"classic"}"""
        repository.refresh()
        engine.response = """{"revision":4,"qrStyleId":"rounded"}"""
        val result = repository.refresh() as Result.Success
        assertEquals(5L, result.data.revision)
        assertEquals("classic", repository.observeBoard().first()?.qrStyleId)
    }

    @Test
    fun conflictPreservesSavedBoardAndReturnsItsBusinessCode() = runBlocking {
        engine.response = """{"revision":5,"qrStyleId":"classic"}"""
        repository.refresh()
        engine.status = HttpStatusCode.Conflict
        engine.response = """{"code":"PROFILE_BOARD_CONFLICT","message":"Reload"}"""
        val result = repository.save(ProfileBoard(revision = 4)) as Result.Failure
        assertEquals("PROFILE_BOARD_CONFLICT", (result.error as DataError.Remote.Business).code)
        assertEquals(5L, repository.observeBoard().first()?.revision)
    }

    @Test
    fun finishedThemedQrIsCachedPerAccount() = runBlocking {
        engine.response = """{"themeId":"vampire_v9","status":"READY","imageUrl":"https://cdn.test/qr.png"}"""
        val result = repository.syncQrRender() as Result.Success
        assertEquals(BoardQrRenderStatus.READY, result.data.status)
        assertEquals("https://cdn.test/qr.png", repository.observeQrRender().first()?.imageUrl)
        currentUser.value = user("second")
        assertNull(repository.observeQrRender().first())
    }

    @Test
    fun recoveredThemedQrReplacesTheCachedImage() = runBlocking {
        engine.response = """{"themeId":"vampire_v9","status":"READY","imageUrl":"https://cdn.test/lost.png"}"""
        repository.syncQrRender()
        engine.response = """{"themeId":"vampire_v9","status":"READY","imageUrl":"https://cdn.test/new.png"}"""
        val result = repository.recoverQrRender() as Result.Success
        assertEquals("https://cdn.test/new.png", result.data.imageUrl)
        assertEquals("https://cdn.test/new.png", repository.observeQrRender().first()?.imageUrl)
    }

    private fun user(id: String) = User(id, "test@example.invalid", "Test", role = "User", status = "Active", isProfileComplete = true, hasVerifiedEmail = true)
}

@OptIn(InternalAPI::class)
private class BoardEngine : HttpClientEngineBase("profile-board-fixture") {
    override val config = HttpClientEngineConfig()
    var response = "{}"
    var status = HttpStatusCode.OK
    override suspend fun execute(data: HttpRequestData) = HttpResponseData(
        status, GMTDate(), headersOf(HttpHeaders.ContentType, "application/json"),
        HttpProtocolVersion.HTTP_1_1, ByteReadChannel(response), callContext(),
    )
}
