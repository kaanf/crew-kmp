package com.kaanf.auth.presentation.social

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SecureRandomTest {
    @Test
    fun platformEntropyPreservesNonceAndPkceEncodingContract() {
        val nonce = randomNonce()
        assertEquals(32, nonce.length)
        assertTrue(nonce.all { it in '0'..'9' || it in 'a'..'f' })
        val verifier = randomNonce() + randomNonce() + randomNonce()
        assertTrue(verifier.length in 43..128)
        assertEquals(48, secureRandomBytes(48).size)
    }

    @Test
    fun emptyEntropyRequestsAreRejected() {
        assertFailsWith<IllegalArgumentException> { secureRandomBytes(0) }
    }
}
