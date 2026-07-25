package com.kaanf.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.auth.domain.model.RegisterParams
import com.kaanf.auth.domain.repository.AuthRepository
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val snackbarController: SnackbarController,
) : ViewModel() {
    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(RegisterState())
    val state =
        _state
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = RegisterState(),
            )

    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnRegisterClick -> {
                submitRegistration()
            }

            RegisterAction.OnLoginClick -> {
                viewModelScope.launch {
                    eventChannel.send(RegisterEvent.NavigateToLogin)
                }
            }

            RegisterAction.OnTogglePasswordVisibilityClick -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            RegisterAction.OnTermsToggle -> {
                _state.update { current ->
                    current.copy(
                        hasAcceptedTerms = !current.hasAcceptedTerms,
                    )
                }
            }

            RegisterAction.OnAgeConfirmationToggle -> {
                _state.update { current ->
                    current.copy(
                        hasConfirmedAge = !current.hasConfirmedAge,
                    )
                }
            }

            is RegisterAction.OnGenderSelect -> {
                _state.update { it.copy(gender = action.gender) }
            }

            RegisterAction.OnUnderageGoBack -> {
                _state.update { it.copy(showUnderageDialog = false) }
                viewModelScope.launch {
                    eventChannel.send(RegisterEvent.NavigateBack)
                }
            }
        }
    }

    private fun submitRegistration() {
        if (_state.value.isRegistering) {
            return
        }

        register()
    }

    private fun register() =
        viewModelScope.launch {
            if (!_state.value.hasAcceptedTerms) {
                return@launch
            }

            if (!_state.value.isPasswordValid) {
                return@launch
            }

            if (!_state.value.isPasswordMatch) {
                return@launch
            }

            if (!_state.value.isEmailValid) {
                return@launch
            }

            if (!_state.value.hasConfirmedAge) {
                return@launch
            }

            val currentState = _state.value

            _state.update {
                it.copy(isRegistering = true)
            }

            try {
                when (
                    val result =
                        authRepository.register(
                            RegisterParams(
                                email = currentState.emailTextState.text.toString().trim(),
                                password = currentState.passwordTextState.text.toString(),
                                fullName = currentState.fullNameTextState.text.toString(),
                                gender = currentState.gender,
                                profilePictureUrl = ""
                            ),
                        )
                ) {
                    is Result.Success -> {
                        eventChannel.send(
                            RegisterEvent.RegisterSuccess(
                                email = currentState.emailTextState.text.toString().trim(),
                            ),
                        )
                    }

                    is Result.Failure -> {
                        // USER_EXISTS dahil iş-kuralı hataları merkezi code→mesaj eşlemesinden gelir.
                        snackbarController.show(result.error.toSnackbarMessage())
                    }
                }
            } finally {
                _state.update {
                    it.copy(isRegistering = false)
                }
            }
        }
}
