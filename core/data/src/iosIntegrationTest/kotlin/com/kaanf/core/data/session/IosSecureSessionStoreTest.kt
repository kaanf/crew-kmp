package com.kaanf.core.data.session

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IosSecureSessionStoreTest {
    @Test
    fun keychainRoundTripsAndOverwritesAnIsolatedSession() = runBlocking {
        val store = IosSecureSessionStore(
            serviceName = "com.kaanf.crew.session.tests",
            accountName = "roundtrip-v1",
        )
        try {
            store.deleteTestEntry()
            assertNull(store.read())
            val first = """{"version":1,"auth":{"token":"synthetic-only","name":"İpek 👋"}}"""
            store.write(first)
            assertEquals(first, store.read())
            val replacement = """{"version":1,"auth":{"token":"synthetic-rotated"}}"""
            store.write(replacement)
            assertEquals(replacement, store.read())
            val signedOut = """{"version":1,"auth":null}"""
            store.write(signedOut)
            assertEquals(signedOut, store.read())
        } finally {
            store.deleteTestEntry()
        }
    }
}
