package com.kaanf.crew.androidapp

import androidx.lifecycle.ViewModelStore
import com.kaanf.auth.presentation.onboarding.OnboardingViewModel
import com.kaanf.core.domain.repository.OnboardingStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val viewModels = ViewModelStore()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        viewModels.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun firstLaunchShowsOnboarding() = runTest(dispatcher) {
        val viewModel = createViewModel(FakeOnboardingStore())
        runCurrent()
        assertEquals(false, viewModel.state.value.completed)
    }

    @Test
    fun returningInstallationReplaysOnboarding() = runTest(dispatcher) {
        val viewModel = createViewModel(FakeOnboardingStore(completed = true))
        runCurrent()
        assertEquals(false, viewModel.state.value.completed)
    }

    @Test
    fun completionIsSavedBeforeDismissalAndRepeatedTapsAreIgnored() = runTest(dispatcher) {
        val store = FakeOnboardingStore()
        val viewModel = createViewModel(store)
        runCurrent()
        viewModel.complete()
        viewModel.complete()
        assertTrue(viewModel.state.value.isSaving)
        assertEquals(false, viewModel.state.value.completed)
        runCurrent()
        assertEquals(1, store.saveCount)
        assertTrue(store.completed.value)
        assertEquals(true, viewModel.state.value.completed)
        assertFalse(viewModel.state.value.isSaving)

        val nextLaunch = createViewModel(store, "nextLaunch")
        runCurrent()
        assertEquals(false, nextLaunch.state.value.completed)
    }

    @Test
    fun failedSaveKeepsOnboardingAndCanBeRetried() = runTest(dispatcher) {
        val store = FakeOnboardingStore().apply { failSave = true }
        val viewModel = createViewModel(store)
        runCurrent()
        viewModel.complete()
        runCurrent()
        assertEquals(false, viewModel.state.value.completed)
        assertTrue(viewModel.state.value.hasError)
        assertFalse(viewModel.state.value.isSaving)
        store.failSave = false
        viewModel.complete()
        runCurrent()
        assertEquals(true, viewModel.state.value.completed)
        assertFalse(viewModel.state.value.hasError)
    }

    private fun createViewModel(store: OnboardingStore, key: String = "onboarding") =
        OnboardingViewModel(store).also { viewModels.put(key, it) }

    private class FakeOnboardingStore(completed: Boolean = false) : OnboardingStore {
        val completed = MutableStateFlow(completed)
        var saveCount = 0
        var failSave = false

        override fun observeCompleted() = completed

        override suspend fun complete() {
            saveCount++
            check(!failSave) { "Preference write failed" }
            completed.value = true
        }
    }
}
