package com.kaanf.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.auth.domain.model.RegisterParams
import com.kaanf.auth.domain.repository.AuthRepository
import com.kaanf.auth.presentation.util.toLocalDate
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.base.BaseEvent
import com.kaanf.core.presentation.model.SnackbarMessage
import com.kaanf.core.presentation.model.SnackbarVariant
import com.kaanf.core.presentation.util.UIText
import com.kaanf.core.presentation.util.toUiText
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.error_accept_terms
import crew.feature.auth.presentation.generated.resources.error_invalid_date_of_birth
import crew.feature.auth.presentation.generated.resources.error_invalid_email
import crew.feature.auth.presentation.generated.resources.error_invalid_password
import crew.feature.auth.presentation.generated.resources.error_password_mismatch
import crew.feature.auth.presentation.generated.resources.snackbar_input_warning_title
import crew.feature.auth.presentation.generated.resources.snackbar_uplink_failure_title
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val eventChannel = Channel<BaseEvent>()
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
                eventChannel.send(
                    BaseEvent.ShowSnackbar(
                        SnackbarMessage(
                            title = UIText.Resource(Res.string.snackbar_input_warning_title),
                            description = UIText.Resource(Res.string.error_accept_terms),
                            variant = SnackbarVariant.Warning,
                        ),
                    ),
                )

                return@launch
            }

            if (!_state.value.isPasswordValid) {
                eventChannel.send(
                    BaseEvent.ShowSnackbar(
                        SnackbarMessage(
                            title = UIText.Resource(Res.string.snackbar_input_warning_title),
                            description = UIText.Resource(Res.string.error_invalid_password),
                            variant = SnackbarVariant.Warning,
                        ),
                    ),
                )

                return@launch
            }

            if (!_state.value.isPasswordMatch) {
                eventChannel.send(
                    BaseEvent.ShowSnackbar(
                        SnackbarMessage(
                            title = UIText.Resource(Res.string.snackbar_input_warning_title),
                            description = UIText.Resource(Res.string.error_password_mismatch),
                            variant = SnackbarVariant.Warning,
                        ),
                    ),
                )

                return@launch
            }

            if (!_state.value.isEmailValid) {
                eventChannel.send(
                    BaseEvent.ShowSnackbar(
                        SnackbarMessage(
                            title = UIText.Resource(Res.string.snackbar_input_warning_title),
                            description = UIText.Resource(Res.string.error_invalid_email),
                            variant = SnackbarVariant.Warning,
                        ),
                    ),
                )

                return@launch
            }

            val currentState = _state.value

            val gender = currentState.gender ?: return@launch

            val dateOfBirth = currentState.dateOfBirthTextState.text.toString().toLocalDate()
            if (dateOfBirth == null) {
                eventChannel.send(
                    BaseEvent.ShowSnackbar(
                        SnackbarMessage(
                            title = UIText.Resource(Res.string.snackbar_input_warning_title),
                            description = UIText.Resource(Res.string.error_invalid_date_of_birth),
                            variant = SnackbarVariant.Warning,
                        ),
                    ),
                )

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
                                email = currentState.emailTextState.text.toString(),
                                password = currentState.passwordTextState.text.toString(),
                                fullName = currentState.fullNameTextState.text.toString(),
                                dateOfBirth = dateOfBirth,
                                gender = gender,
                            ),
                        )
                ) {
                    is Result.Success -> {
                        eventChannel.send(
                            RegisterEvent.RegisterSuccess(
                                email = currentState.emailTextState.text.toString(),
                            ),
                        )
                    }

                    is Result.Failure -> {
                        eventChannel.send(
                            BaseEvent.ShowSnackbar(
                                SnackbarMessage(
                                    title = UIText.Resource(Res.string.snackbar_uplink_failure_title),
                                    description = result.error.toUiText(),
                                    variant = SnackbarVariant.Warning,
                                ),
                            ),
                        )
                    }
                }
            } finally {
                _state.update {
                    it.copy(isRegistering = false)
                }
            }
        }
}
