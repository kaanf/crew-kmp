package com.kaanf.core.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kaanf.core.data.mappers.toSerializable
import com.kaanf.core.domain.model.auth.AuthInfo
import com.kaanf.core.domain.model.user.User
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DataStoreSessionStorageTest {
    @Test
    fun `legacy session is protected before plaintext is removed`() = runBlocking {
        val auth = auth()
        val legacy = legacy(auth)
        val secure = FakeSecureStore()
        var excluded = false
        val storage = storage(legacy, secure, onCleared = { excluded = true })

        assertEquals(auth, storage.snapshot().authInfo)
        assertNotNull(secure.value)
        assertNull(legacy.current[AUTH_KEY])
        assertTrue(excluded)
        assertEquals(auth, storage(legacy, secure).snapshot().authInfo)
    }

    @Test
    fun `failed secure write preserves legacy data for a safe retry`() = runBlocking {
        val legacy = legacy(auth())
        val secure = FakeSecureStore(failWrites = true)
        var failed = false
        try {
            storage(legacy, secure).snapshot()
        } catch (_: IllegalStateException) {
            failed = true
        }
        assertTrue(failed)
        assertNotNull(legacy.current[AUTH_KEY])
        secure.failWrites = false
        assertEquals(auth(), storage(legacy, secure).snapshot().authInfo)
        assertNull(legacy.current[AUTH_KEY])
    }

    @Test
    fun `signed out marker prevents restored legacy credentials from returning`() = runBlocking {
        val secure = FakeSecureStore()
        val first = storage(legacy(auth()), secure)
        first.snapshot()
        first.set(null)
        // A backup from the old app may still contain credentials. Protected state is authoritative.
        val restored = legacy(auth())
        assertNull(storage(restored, secure).snapshot().authInfo)
        assertNull(restored.current[AUTH_KEY])
    }

    @Test
    fun `failed plaintext cleanup retries without replacing protected session`() = runBlocking {
        val secure = FakeSecureStore()
        val legacy = legacy(auth())
        legacy.failUpdates = true
        var failed = false
        try {
            storage(legacy, secure).snapshot()
        } catch (_: IllegalStateException) {
            failed = true
        }
        assertTrue(failed)
        assertNotNull(secure.value)
        assertNotNull(legacy.current[AUTH_KEY])
        legacy.failUpdates = false
        assertEquals(auth(), storage(legacy, secure).snapshot().authInfo)
        assertNull(legacy.current[AUTH_KEY])
    }

    @Test
    fun `refresh completion cannot restore a logged out session`() = runBlocking {
        val storage = storage(legacy(auth()), FakeSecureStore())
        val request = storage.snapshot()
        storage.set(null)
        assertFalse(storage.compareAndSet(request, auth("rotated")))
        assertNull(storage.snapshot().authInfo)
    }

    @Test
    fun `refresh failure cannot erase a newer login even with identical credentials`() = runBlocking {
        val storage = storage(legacy(auth()), FakeSecureStore())
        val request = storage.snapshot()
        storage.set(null)
        storage.set(auth())
        assertFalse(storage.compareAndSet(request, null))
        assertEquals(auth(), storage.snapshot().authInfo)
        assertFalse(storage.compareAndSet(request, auth("stale-rotation")))
    }

    @Test
    fun `successful rotation consumes snapshot and rejects a later stale response`() = runBlocking {
        val storage = storage(legacy(auth()), FakeSecureStore())
        val request = storage.snapshot()
        assertTrue(storage.compareAndSet(request, auth("rotated")))
        assertFalse(storage.compareAndSet(request, null))
        assertEquals(auth("rotated"), storage.snapshot().authInfo)
    }

    @Test
    fun `profile changes do not invalidate pending credential rotation`() = runBlocking {
        val storage = storage(legacy(auth()), FakeSecureStore())
        val request = storage.snapshot()
        storage.update { current -> current?.copy(user = current.user.copy(fullName = "Updated")) }
        assertTrue(storage.compareAndSet(request, auth("rotated")))
        assertEquals("Updated", storage.snapshot().authInfo?.user?.fullName)
    }

    @Test
    fun `logout invalidates rotation completed after logout captured its session`() = runBlocking {
        val storage = storage(legacy(auth()), FakeSecureStore())
        val logout = storage.snapshot()
        val refresh = storage.snapshot()
        assertTrue(storage.compareAndSet(refresh, auth("rotated")))
        assertTrue(storage.clearSession(logout))
        assertNull(storage.snapshot().authInfo)
    }

    @Test
    fun `old logout or delete response cannot clear a newer login`() = runBlocking {
        val storage = storage(legacy(auth()), FakeSecureStore())
        val logout = storage.snapshot()
        storage.set(null)
        storage.set(auth())
        assertFalse(storage.clearSession(logout))
        assertEquals(auth(), storage.snapshot().authInfo)
    }

    @Test
    fun `cancellation after durable signout still publishes state and invalidates token cache`() = runBlocking {
        val secure = FakeSecureStore()
        var invalidations = 0
        val storage = DataStoreSessionStorage(legacy(auth()), secure, {}) { invalidations++ }
        val before = storage.snapshot()
        val logout = launch {
            val owner = coroutineContext
            secure.afterWrite = { owner.cancel() }
            storage.set(null)
        }
        logout.join()
        assertTrue(logout.isCancelled)
        assertNull(storage.snapshot().authInfo)
        assertTrue(storage.snapshot().generation > before.generation)
        assertEquals(1, invalidations)
        secure.afterWrite = {}
        assertNull(storage(MemoryPreferences(), secure).snapshot().authInfo)
    }

    @Test
    fun `failed secure persistence does not publish a changed session`() = runBlocking {
        val secure = FakeSecureStore()
        val storage = storage(legacy(auth()), secure)
        val before = storage.snapshot()
        secure.failWrites = true
        var failed = false
        try {
            storage.set(null)
        } catch (_: IllegalStateException) {
            failed = true
        }
        assertTrue(failed)
        assertEquals(before, storage.snapshot())
    }

    private fun auth(suffix: String = "original") = AuthInfo(
        accessToken = "synthetic-access-$suffix",
        refreshToken = "synthetic-refresh-$suffix",
        user = User("synthetic-user", "test@example.invalid", "Test", role = "User", status = "Active", isProfileComplete = true, hasVerifiedEmail = true),
    )

    private fun legacy(auth: AuthInfo): MemoryPreferences = MemoryPreferences().apply {
        current = emptyPreferences().toMutablePreferences().apply {
            this[AUTH_KEY] = Json.encodeToString(auth.toSerializable())
        }
    }

    private fun storage(
        legacy: MemoryPreferences,
        secure: FakeSecureStore,
        onCleared: () -> Unit = {},
    ) = DataStoreSessionStorage(legacy, secure, onCleared) {}

    private class FakeSecureStore(var value: String? = null, var failWrites: Boolean = false) : SecureSessionStore {
        var afterWrite: () -> Unit = {}
        override suspend fun read(): String? = value
        override suspend fun write(value: String) {
            check(!failWrites) { "Simulated secure storage failure" }
            this.value = value
            afterWrite()
        }
    }

    private class MemoryPreferences : DataStore<Preferences> {
        private val state = MutableStateFlow(emptyPreferences())
        var failUpdates = false
        var current: Preferences
            get() = state.value
            set(value) { state.value = value }
        override val data: Flow<Preferences> = state
        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            check(!failUpdates) { "Simulated migration interruption" }
            current = transform(current)
            return current
        }
    }

    private companion object {
        val AUTH_KEY = stringPreferencesKey("KEY_AUTH_INFO")
    }
}
