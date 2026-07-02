package com.kaanf.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.auth.domain.model.RegisterParams
import com.kaanf.auth.domain.repository.AuthRepository
import com.kaanf.auth.presentation.util.toLocalDate
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.core.presentation.util.UIText
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.error_email_exists
import crew.feature.auth.presentation.generated.resources.register_snackbar_email_taken_title
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

            is RegisterAction.OnGenderSelect -> {
                _state.update { it.copy(gender = action.gender) }
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

            val currentState = _state.value

            val gender = currentState.gender ?: return@launch

            val dateOfBirth = currentState.dateOfBirthTextState.text.toString().toLocalDate()
            if (dateOfBirth == null) {
                return@launch
            }

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
                                dateOfBirth = dateOfBirth,
                                gender = gender,
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
                        val message = when (result.error) {
                            DataError.Remote.CONFLICT -> SnackbarMessage(
                                title = UIText.Resource(Res.string.register_snackbar_email_taken_title),
                                description = UIText.Resource(Res.string.error_email_exists),
                                variant = SnackbarVariant.Warn,
                            )

                            else -> result.error.toSnackbarMessage()
                        }
                        snackbarController.show(message)
                    }
                }
            } finally {
                _state.update {
                    it.copy(isRegistering = false)
                }
            }
        }
}
