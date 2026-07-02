package com.kaanf.core.domain.repository

import com.kaanf.core.domain.model.settings.AppLanguage
import kotlinx.coroutines.flow.Flow

interface LanguageStore {
    fun observeLanguage(): Flow<AppLanguage>
    suspend fun setLanguage(language: AppLanguage)
}
