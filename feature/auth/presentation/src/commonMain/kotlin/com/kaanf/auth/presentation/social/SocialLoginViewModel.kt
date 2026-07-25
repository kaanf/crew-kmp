package com.kaanf.auth.presentation.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.auth.domain.model.SocialLoginParams
import com.kaanf.auth.domain.repository.AuthRepository
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.core.presentation.util.UIText
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.social_sign_in_failed_description
import crew.feature.auth.presentation.generated.resources.social_sign_in_failed_title
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SocialLoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionStorage: SessionStorage,
    private val snackbarController: SnackbarController,
) : ViewModel() {
    private val eventChannel = Channel<SocialLoginEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(SocialLoginState())
    val state =
        _state
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = SocialLoginState(),
            )

    fun onAction(action: SocialLoginAction) {
        when (action) {
            is SocialLoginAction.OnProviderClick -> {
                if (_state.value.isSubmitting) return
                // Onay, provider akışından ÖNCE alınır: kullanıcı politikayı okurken
                // kısa ömürlü (~5 dk) id_token'ın süresi dolmasın (PRD §5.2).
                _state.update { it.copy(consentProvider = action.provider) }
            }

            SocialLoginAction.OnConsentDismiss -> {
                _state.update { it.copy(consentProvider = null) }
            }

            SocialLoginAction.OnConsentConfirm -> {
                val provider = _state.value.consentProvider ?: return
                _state.update { it.copy(consentProvider = null, pendingProvider = provider) }
                viewModelScope.launch {
                    eventChannel.send(SocialLoginEvent.LaunchProvider(provider))
                }
            }

            is SocialLoginAction.OnSignInResult -> {
                handleSignInResult(action.result)
            }
        }
    }

    private fun handleSignInResult(result: SocialSignInResult) {
        when (result) {
            SocialSignInResult.Cancelled -> {
                _state.update { it.copy(pendingProvider = null) }
            }

            SocialSignInResult.Failed -> {
                _state.update { it.copy(pendingProvider = null) }
                viewModelScope.launch {
                    snackbarController.show(socialSignInFailedMessage())
                }
            }

            is SocialSignInResult.Success -> {
                submit(result)
            }
        }
    }

    private fun submit(result: SocialSignInResult.Success) =
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }

            try {
                val loginResult =
                    authRepository.socialLogin(
                        SocialLoginParams(
                            provider = result.provider,
                            idToken = result.idToken,
                            nonce = result.nonce,
                            // Sheet her iki kutu işaretlenmeden onaylanamaz; backend yine de doğrular.
                            ageConfirmed = true,
                            privacyAccepted = true,
                            fullName = result.fullName,
                        ),
                    )

                when (loginResult) {
                    is Result.Success -> {
                        sessionStorage.set(loginResult.data)
                        if (loginResult.data.user.isProfileComplete) {
                            eventChannel.send(SocialLoginEvent.NavigateToDashboard)
                        } else {
                            eventChannel.send(SocialLoginEvent.NavigateToProfilePicture)
                        }
                    }

                    is Result.Failure -> {
                        // 401 = provider token'ı geçersiz ya da süresi dolmuş; genel "unauthorized"
                        // mesajı oturum sorunu gibi okunur, girişe özel mesaj göster.
                        // İş-kuralı hataları (409/400) merkezi code→mesaj eşlemesinden gelir.
                        val message = when (loginResult.error) {
                            DataError.Remote.UNAUTHORIZED -> socialSignInFailedMessage()
                            else -> loginResult.error.toSnackbarMessage()
                        }
                        snackbarController.show(message)
                    }
                }
            } finally {
                _state.update { it.copy(isSubmitting = false, pendingProvider = null) }
            }
        }

    private fun socialSignInFailedMessage() =
        SnackbarMessage(
            title = UIText.Resource(Res.string.social_sign_in_failed_title),
            description = UIText.Resource(Res.string.social_sign_in_failed_description),
            variant = SnackbarVariant.Error,
        )
}
