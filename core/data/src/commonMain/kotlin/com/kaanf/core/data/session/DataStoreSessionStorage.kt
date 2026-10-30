package com.kaanf.core.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kaanf.core.data.dto.AuthInfoSerializable
import com.kaanf.core.data.mappers.toDomain
import com.kaanf.core.data.mappers.toSerializable
import com.kaanf.core.domain.model.auth.AuthInfo
import com.kaanf.core.domain.repository.SessionSnapshot
import com.kaanf.core.domain.repository.SessionStorage
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Preferences is used only to migrate and erase the old plaintext session. */
class DataStoreSessionStorage(
    private val dataStore: DataStore<Preferences>,
    private val secureStore: SecureSessionStore,
    private val onLegacyCleared: () -> Unit = ::excludeLegacyPreferencesFromBackup,
    private val onSessionChanged: () -> Unit,
) : SessionStorage {
    private val authInfoKey = stringPreferencesKey("KEY_AUTH_INFO")
    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private val state = MutableStateFlow(SessionSnapshot(null, 0))
    private var initialized = false

    @Serializable
    private data class Envelope(val version: Int = 1, val auth: AuthInfoSerializable? = null)

    override fun observeAuthInfo(): Flow<AuthInfo?> = flow {
        snapshot()
        emitAll(state.map { it.authInfo })
    }

    override suspend fun snapshot(): SessionSnapshot = mutex.withLock {
        initialize()
        state.value
    }

    override suspend fun set(info: AuthInfo?) {
        mutex.withLock {
            initialize()
            persist(info, newGeneration = true)
        }
    }

    override suspend fun update(transform: (AuthInfo?) -> AuthInfo?) {
        mutex.withLock {
            initialize()
            val current = state.value.authInfo
            val next = transform(current)
            // Profile-only edits preserve the identity of an outstanding refresh.
            val credentialsChanged = current?.accessToken != next?.accessToken ||
                current?.refreshToken != next?.refreshToken
            persist(next, newGeneration = credentialsChanged)
        }
    }

    override suspend fun clearSession(expected: SessionSnapshot): Boolean {
        val changed = mutex.withLock {
            initialize()
            if (state.value.generation != expected.generation) {
                false
            } else {
                persist(null, newGeneration = true)
                true
            }
        }
        return changed
    }

    override suspend fun compareAndSet(expected: SessionSnapshot, info: AuthInfo?): Boolean {
        val changed = mutex.withLock {
            initialize()
            val current = state.value
            if (current.generation != expected.generation ||
                current.authInfo?.refreshToken != expected.authInfo?.refreshToken
            ) {
                false
            } else {
                // Token rotation belongs to the same login; logout must still invalidate it.
                // A concurrent profile edit is newer local information than the refresh snapshot.
                val currentAuth = current.authInfo
                val next = if (info != null && currentAuth != null &&
                    currentAuth.user != expected.authInfo?.user && currentAuth.user.id == info.user.id
                ) {
                    info.copy(user = currentAuth.user)
                } else {
                    info
                }
                persist(next, newGeneration = info == null)
                true
            }
        }
        return changed
    }

    private suspend fun initialize() {
        if (initialized) return
        val protected = secureStore.read()
        val info = if (protected == null) {
            val legacy = dataStore.data.first()[authInfoKey]
            val migrated = legacy?.let {
                // A corrupt legacy entry cannot be used as an authenticated session.
                runCatching { json.decodeFromString<AuthInfoSerializable>(it).toDomain() }.getOrNull()
            }
            // Persist first. A crash before erasing Preferences retries cleanup, not migration.
            secureStore.write(json.encodeToString(Envelope(auth = migrated?.toSerializable())))
            migrated
        } else {
            val envelope = json.decodeFromString<Envelope>(protected)
            check(envelope.version == 1) { "Unsupported secure session version" }
            envelope.auth?.toDomain()
        }
        dataStore.edit { it.remove(authInfoKey) }
        onLegacyCleared()
        state.value = SessionSnapshot(info, 0)
        initialized = true
    }

    private suspend fun persist(info: AuthInfo?, newGeneration: Boolean) = withContext(NonCancellable) {
        // Once secure persistence commits, cancellation must not leave the live session/cache stale.
        secureStore.write(json.encodeToString(Envelope(auth = info?.toSerializable())))
        val generation = state.value.generation + if (newGeneration) 1 else 0
        state.value = SessionSnapshot(info, generation)
        onSessionChanged()
    }
}
