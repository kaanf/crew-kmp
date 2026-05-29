package com.kaanf.auth.presentation.emailverification.verificationresult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.auth.domain.repository.AuthRepository
import com.kaanf.core.domain.util.onFailure
import com.kaanf.core.domain.util.onSuccess
import com.kaanf.core.presentation.base.BaseEvent
import com.kaanf.core.presentation.model.SnackbarMessage
import com.kaanf.core.presentation.model.SnackbarVariant
import com.kaanf.core.presentation.util.UIText
import com.kaanf.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmailVerificationResultViewModel(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val verificationToken = savedStateHandle.get<String>("token")
    private var hasNavigatedToLogin = false

    private val eventChannel = Channel<BaseEvent>()
    val events
        get() = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(EmailVerificationResultState())
    val state = _state.asStateFlow()

    init {
        startVerification()
    }

    fun onAction(action: EmailVerificationResultAction) {
        when (action) {
            EmailVerificationResultAction.OnReturnToLoginClicked -> {

            }
            EmailVerificationResultAction.OnResendSignalClicked -> TODO()
        }
    }

    private fun startVerification() =
        viewModelScope.launch {
            val token = verificationToken

            if (token.isNullOrBlank()) {
                _state.update { current ->
                    current.copy(
                        phase = EmailVerificationPhase.Failed,
                    )
                }

                eventChannel.send(
                    BaseEvent.ShowSnackbar(
                        SnackbarMessage(
                            title = UIText.DynamicString("Verification token is missing."),
                            description = UIText.DynamicString("Verification token is missing."),
                            variant = SnackbarVariant.Failure,
                        ),
                    ),
                )

                return@launch
            }

            delay(1000L)

            authRepository
                .verifyEmail(token)
                .onSuccess {
                    _state.update { current ->
                        current.copy(
                            phase = EmailVerificationPhase.Verified,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { current ->
                        current.copy(
                            phase = EmailVerificationPhase.Failed,
                        )
                    }

                    eventChannel.send(
                        BaseEvent.ShowSnackbar(
                            SnackbarMessage(
                                title = error.toUiText(),
                                description = error.toUiText(),
                                variant = SnackbarVariant.Failure,
                            ),
                        ),
                    )
                }
        }
}
