package com.kaanf.core.data.session

/** Stores one versioned session envelope, including an explicit signed-out marker. */
interface SecureSessionStore {
    suspend fun read(): String?
    suspend fun write(value: String)
}

/** The legacy preferences may have contained credentials before migration. */
expect fun excludeLegacyPreferencesFromBackup()
