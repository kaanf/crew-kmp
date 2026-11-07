package com.kaanf.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kaanf.core.data.dto.BoardQrRenderDto
import com.kaanf.core.data.dto.ProfileBoardDto
import com.kaanf.core.data.dto.toDto
import com.kaanf.core.data.networking.get
import com.kaanf.core.data.networking.post
import com.kaanf.core.data.networking.put
import com.kaanf.core.domain.model.profileboard.BoardQrRender
import com.kaanf.core.domain.model.profileboard.BoardQrRenderStatus
import com.kaanf.core.domain.model.profileboard.ProfileBoard
import com.kaanf.core.domain.repository.ProfileBoardRepository
import com.kaanf.core.domain.repository.UserStore
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import io.ktor.client.HttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ProfileBoardRepositoryImpl(
    private val httpClient: HttpClient,
    private val userStore: UserStore,
    private val dataStore: DataStore<Preferences>,
) : ProfileBoardRepository {
    private val requestMutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }
    private fun key(userId: String) = stringPreferencesKey("PROFILE_BOARD_$userId")
    private fun qrKey(userId: String) = stringPreferencesKey("PROFILE_BOARD_QR_$userId")

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeBoard(): Flow<ProfileBoard?> = userStore.observeCurrentUser().map { it?.id }.distinctUntilChanged()
        .flatMapLatest { userId ->
            if (userId == null) flowOf(null) else dataStore.data.map { preferences ->
                preferences[key(userId)]?.let { cached ->
                    runCatching { json.decodeFromString<ProfileBoardDto>(cached).toDomain() }.getOrNull()
                }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeQrRender(): Flow<BoardQrRender?> = userStore.observeCurrentUser().map { it?.id }.distinctUntilChanged()
        .flatMapLatest { userId ->
            if (userId == null) flowOf(null) else dataStore.data.map { preferences ->
                preferences[qrKey(userId)]?.let { cached ->
                    runCatching { json.decodeFromString<BoardQrRenderDto>(cached).toDomain() }.getOrNull()
                }
            }
        }

    override suspend fun syncQrRender(): Result<BoardQrRender, DataError.Remote> {
        val userId = userStore.observeCurrentUser().first()?.id ?: return Result.Failure(DataError.Remote.UNAUTHORIZED)
        var attempt = 0
        while (true) {
            val result = httpClient.get<BoardQrRenderDto>("/profile/board/qr")
            if (result is Result.Failure) return result
            val render = (result as Result.Success).data
            dataStore.edit { it[qrKey(userId)] = json.encodeToString(render) }
            val domain = render.toDomain()
            if (domain.status != BoardQrRenderStatus.PENDING || ++attempt >= MAX_QR_POLLS) return Result.Success(domain)
            delay(QR_POLL_INTERVAL_MS)
        }
    }

    override suspend fun recoverQrRender(): Result<BoardQrRender, DataError.Remote> {
        val userId = userStore.observeCurrentUser().first()?.id ?: return Result.Failure(DataError.Remote.UNAUTHORIZED)
        val result = httpClient.post<BoardQrRenderDto>("/profile/board/qr/recover")
        if (result is Result.Failure) return result
        val render = (result as Result.Success).data
        dataStore.edit { it[qrKey(userId)] = json.encodeToString(render) }
        val domain = render.toDomain()
        return if (domain.status == BoardQrRenderStatus.PENDING) syncQrRender() else Result.Success(domain)
    }

    override suspend fun refresh(): Result<ProfileBoard, DataError.Remote> = requestMutex.withLock {
        val userId = userStore.observeCurrentUser().first()?.id ?: return@withLock Result.Failure(DataError.Remote.UNAUTHORIZED)
        cache(userId, httpClient.get<ProfileBoardDto>("/profile/board"))
    }

    override suspend fun save(board: ProfileBoard): Result<ProfileBoard, DataError.Remote> = requestMutex.withLock {
        val userId = userStore.observeCurrentUser().first()?.id ?: return@withLock Result.Failure(DataError.Remote.UNAUTHORIZED)
        cache(userId, httpClient.put<ProfileBoardDto, ProfileBoardDto>(route = "/profile/board", body = board.toDto()))
    }

    private suspend fun cache(userId: String, result: Result<ProfileBoardDto, DataError.Remote>): Result<ProfileBoard, DataError.Remote> {
        if (result is Result.Failure) return result
        var accepted = (result as Result.Success).data
        dataStore.edit { preferences ->
            val cached = preferences[key(userId)]?.let {
                runCatching { json.decodeFromString<ProfileBoardDto>(it) }.getOrNull()
            }
            if (cached != null && cached.revision > accepted.revision) accepted = cached
            else preferences[key(userId)] = json.encodeToString(accepted)
        }
        return Result.Success(accepted.toDomain())
    }

    private companion object {
        const val QR_POLL_INTERVAL_MS = 3_000L
        const val MAX_QR_POLLS = 60
    }
}
