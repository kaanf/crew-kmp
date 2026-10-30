package com.kaanf.auth.presentation.social

import java.security.SecureRandom

internal actual fun secureRandomBytes(size: Int): ByteArray {
    require(size > 0)
    return ByteArray(size).also { SecureRandom().nextBytes(it) }
}
