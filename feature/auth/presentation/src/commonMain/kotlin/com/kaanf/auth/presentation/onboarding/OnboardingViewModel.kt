package com.kaanf.auth.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.core.domain.repository.OnboardingStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Temporary preview mode: show onboarding again whenever the auth flow is opened.
private const val REPLAY_ONBOARDING = true

data class OnboardingState(
    val completed: Boolean? = null,
    val isSaving: Boolean = false,
    val hasError: Boolean = false,
)

class OnboardingViewModel(
    private val store: OnboardingStore,
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    init {
        if (REPLAY_ONBOARDING) {
            _state.update { it.copy(completed = false) }
        } else {
            observeCompletion()
        }
    }

    private fun observeCompletion() {
        viewModelScope.launch {
            try {
                store.observeCompleted().collect { completed ->
                    _state.update { it.copy(completed = completed) }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // A failed preference read must not strand the user on an empty screen.
                _state.update { it.copy(completed = false) }
            }
        }
    }

    fun complete() {
        if (_state.value.isSaving || _state.value.completed == true) return
        _state.update { it.copy(isSaving = true, hasError = false) }
        viewModelScope.launch {
            try {
                store.complete()
                // Keep the preference for when temporary replay mode is disabled.
                _state.update { it.copy(completed = true, isSaving = false) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _state.update { it.copy(isSaving = false, hasError = true) }
            }
        }
    }
}
