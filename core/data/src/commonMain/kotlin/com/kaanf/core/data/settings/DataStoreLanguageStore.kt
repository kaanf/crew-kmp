package com.kaanf.core.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kaanf.core.domain.model.settings.AppLanguage
import com.kaanf.core.domain.repository.LanguageStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreLanguageStore(
    private val dataStore: DataStore<Preferences>,
) : LanguageStore {
    private val languageTagKey = stringPreferencesKey("KEY_APP_LANGUAGE")

    override fun observeLanguage(): Flow<AppLanguage> {
        return dataStore.data.map { preferences ->
            AppLanguage.fromTag(preferences[languageTagKey])
        }
    }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { prefs ->
            prefs[languageTagKey] = language.tag
        }
    }
}
