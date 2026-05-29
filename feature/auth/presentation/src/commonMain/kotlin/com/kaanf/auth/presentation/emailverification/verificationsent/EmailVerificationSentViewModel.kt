package com.kaanf.auth.presentation.emailverification.verificationsent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.auth.domain.repository.AuthRepository
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.base.BaseEvent
import com.kaanf.core.presentation.model.SnackbarMessage
import com.kaanf.core.presentation.model.SnackbarVariant
import com.kaanf.core.presentation.util.UIText
import com.kaanf.core.presentation.util.toUiText
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.snackbar_uplink_failure_title
import crew.feature.auth.presentation.generated.resources.verification_mail_sent_title
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmailVerificationSentViewModel(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val verificationToken = savedStateHandle.get<String>("token")

    private val email =
        savedStateHandle.get<String>("email")
            ?: throw IllegalStateException("No email passed to register success screen")

    private val eventChannel = Channel<BaseEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state =
        MutableStateFlow(
            EmailVerificationSentState(
                registeredEmail = email,
            ),
        )

    val state =
        _state.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    init {
        startResendCountdown()
    }

    fun onAction(action: EmailVerificationSentAction) {
        when (action) {
            EmailVerificationSentAction.OnResendSignalClicked -> {
                resendVerificationSignal()
            }

            EmailVerificationSentAction.OnReturnToLoginClicked -> {
                viewModelScope.launch {
                    eventChannel.send(EmailVerificationSentEvent.NavigateToLogin)
                }
            }
        }
    }

    private fun resendVerificationSignal() =
        viewModelScope.launch {
            val currentState = _state.value

            if (currentState.isResending || !currentState.isResendEnabled) {
                return@launch
            }

            _state.update {
                it.copy(
                    isResending = true,
                    isResendEnabled = false,
                )
            }

            try {
                when (val result = authRepository.resendVerificationMail(email)) {
                    is Result.Success -> {
                        eventChannel.send(
                            BaseEvent.ShowSnackbar(
                                SnackbarMessage(
                                    title = UIText.Resource(Res.string.verification_mail_sent_title),
                                    description = UIText.Resource(Res.string.verification_mail_sent_title),
                                    variant = SnackbarVariant.Success,
                                ),
                            ),
                        )

                        startResendCountdown()
                    }

                    is Result.Failure -> {
                        _state.update { state ->
                            state.copy(isResendEnabled = true)
                        }

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
                    it.copy(isResending = false)
                }
            }
        }

    private var resendCountdownJob: Job? = null

    companion object {
        private const val RESEND_COUNTDOWN_SECONDS = 60
    }

    private fun startResendCountdown(durationSeconds: Int = RESEND_COUNTDOWN_SECONDS) {
        resendCountdownJob?.cancel()
        resendCountdownJob =
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isResendEnabled = false,
                        resendCountdownSeconds = durationSeconds,
                    )
                }

                for (remainingSeconds in (durationSeconds - 1) downTo 0) {
                    delay(1_000L)
                    _state.update { state ->
                        state.copy(
                            resendCountdownSeconds = remainingSeconds,
                            isResendEnabled = remainingSeconds == 0,
                        )
                    }
                }
            }
    }
}
