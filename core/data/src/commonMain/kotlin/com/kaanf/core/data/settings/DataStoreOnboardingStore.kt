package com.kaanf.core.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.kaanf.core.domain.repository.OnboardingStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreOnboardingStore(
    private val dataStore: DataStore<Preferences>,
) : OnboardingStore {
    private val completedKey = booleanPreferencesKey("KEY_ONBOARDING_COMPLETED")

    override fun observeCompleted(): Flow<Boolean> =
        dataStore.data.map { it[completedKey] ?: false }

    override suspend fun complete() {
        dataStore.edit { it[completedKey] = true }
    }
}
