package com.kaanf.core.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kaanf.core.data.dto.AuthInfoSerializable
import com.kaanf.core.data.mappers.toDomain
import com.kaanf.core.data.mappers.toSerializable
import com.kaanf.core.domain.model.auth.AuthInfo
import com.kaanf.core.domain.repository.SessionStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class DataStoreSessionStorage(
    private val dataStore: DataStore<Preferences>,
) : SessionStorage {
    private val authInfoKey = stringPreferencesKey("KEY_AUTH_INFO")

    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    override fun observeAuthInfo(): Flow<AuthInfo?> {
        return dataStore.data.map { preferences ->
            val serializedJson = preferences[authInfoKey]
            serializedJson?.let {
                json.decodeFromString<AuthInfoSerializable>(it).toDomain()
            }
        }
    }

    override suspend fun set(info: AuthInfo?) {
        update { info }
    }

    override suspend fun update(transform: (AuthInfo?) -> AuthInfo?) {
        dataStore.edit { prefs ->
            val current = prefs[authInfoKey]?.let {
                json.decodeFromString<AuthInfoSerializable>(it).toDomain()
            }
            val updated = transform(current)
            if (updated == null) {
                prefs.remove(authInfoKey)
            } else {
                prefs[authInfoKey] = json.encodeToString(updated.toSerializable())
            }
        }
    }
}
