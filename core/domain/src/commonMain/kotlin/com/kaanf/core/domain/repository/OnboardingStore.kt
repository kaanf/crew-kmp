package com.kaanf.core.domain.repository

import kotlinx.coroutines.flow.Flow

/** Installation-level preference, independent of the signed-in account. */
interface OnboardingStore {
    fun observeCompleted(): Flow<Boolean>

    suspend fun complete()
}
