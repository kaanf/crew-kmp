package com.kaanf.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.auth.domain.repository.AuthRepository
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.base.BaseEvent
import com.kaanf.core.presentation.model.SnackbarMessage
import com.kaanf.core.presentation.model.SnackbarVariant
import com.kaanf.core.presentation.util.UIText
import com.kaanf.core.presentation.util.toUiText
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.login_snackbar_auth_failed_title
import crew.feature.auth.presentation.generated.resources.login_snackbar_invalid_email_description
import crew.feature.auth.presentation.generated.resources.login_snackbar_invalid_email_title
import crew.feature.auth.presentation.generated.resources.login_snackbar_invalid_password_description
import crew.feature.auth.presentation.generated.resources.login_snackbar_invalid_password_title
import crew.feature.auth.presentation.generated.resources.login_snackbar_success_description
import crew.feature.auth.presentation.generated.resources.login_snackbar_success_title
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionStorage: SessionStorage,
) : ViewModel() {
    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(LoginState())
    val state =
        _state.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LoginState(),
        )

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnLoginClick -> {
                login()
            }

            LoginAction.OnRegisterClick -> {
                viewModelScope.launch {
                    eventChannel.send(LoginEvent.NavigateToRegister)
                }
            }

            LoginAction.OnForgotPasswordClick -> {
                viewModelScope.launch {
                    eventChannel.send(LoginEvent.NavigateToForgotPassword)
                }
            }
        }
    }

    private fun login() =
        viewModelScope.launch {
            val currentState = _state.value

            if (currentState.isSubmitting) {
                return@launch
            }

            if (!currentState.isEmailValid) {
                return@launch
            }

            if (!currentState.isPasswordValid) {
                return@launch
            }

            _state.update {
                it.copy(
                    isSubmitting = true,
                )
            }

            try {
                when (
                    val result =
                        authRepository.login(
                            email = currentState.emailTextState.text.toString(),
                            password = currentState.passwordTextState.text.toString(),
                        )
                ) {
                    is Result.Success -> {
                        eventChannel.send(LoginEvent.NavigateToDashboard)
                        sessionStorage.set(result.data)
                    }

                    is Result.Failure -> {
                    }
                }
            } finally {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                    )
                }
            }
        }
}
