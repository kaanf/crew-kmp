package com.kaanf.auth.presentation.signinmethods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaanf.auth.domain.model.LinkIdentityParams
import com.kaanf.auth.domain.model.SocialProvider
import com.kaanf.auth.domain.repository.AuthRepository
import com.kaanf.auth.presentation.social.SocialSignInResult
import com.kaanf.core.domain.repository.SessionStorage
import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.SnackbarMessage
import com.kaanf.core.presentation.snackbar.SnackbarVariant
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.core.presentation.util.UIText
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.sign_in_methods_linked_description
import crew.feature.auth.presentation.generated.resources.sign_in_methods_linked_title
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_saved_description
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_saved_title
import crew.feature.auth.presentation.generated.resources.sign_in_methods_unlinked_description
import crew.feature.auth.presentation.generated.resources.sign_in_methods_unlinked_title
import crew.feature.auth.presentation.generated.resources.social_sign_in_failed_description
import crew.feature.auth.presentation.generated.resources.social_sign_in_failed_title
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val IDENTITY_ALREADY_LINKED = "IDENTITY_ALREADY_LINKED"

class SignInMethodsViewModel(
    private val authRepository: AuthRepository,
    private val sessionStorage: SessionStorage,
    private val snackbarController: SnackbarController,
) : ViewModel() {
    private val eventChannel = Channel<SignInMethodsEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(SignInMethodsState())
    val state =
        _state
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = SignInMethodsState(),
            )

    init {
        load()
    }

    fun onAction(action: SignInMethodsAction) {
        when (action) {
            is SignInMethodsAction.OnConnectClick -> connect(action.provider)
            is SignInMethodsAction.OnSignInResult -> handleSignInResult(action.result)
            is SignInMethodsAction.OnUnlinkClick ->
                _state.update { it.copy(unlinkTarget = action.provider) }

            SignInMethodsAction.OnUnlinkDismiss -> _state.update { it.copy(unlinkTarget = null) }
            SignInMethodsAction.OnUnlinkConfirm -> unlink()
            SignInMethodsAction.OnConflictDismiss -> _state.update { it.copy(conflictProvider = null) }

            SignInMethodsAction.OnPasswordClick -> {
                val mode = if (_state.value.methods?.hasPassword == true) {
                    PasswordSheetMode.Change
                } else {
                    PasswordSheetMode.Set
                }
                _state.update { it.copy(passwordSheet = mode) }
            }

            SignInMethodsAction.OnPasswordSheetDismiss ->
                if (!_state.value.isSavingPassword) _state.update { it.copy(passwordSheet = null) }

            is SignInMethodsAction.OnPasswordSubmit ->
                savePassword(action.currentPassword, action.newPassword)
        }
    }

    private fun load() =
        viewModelScope.launch {
            when (val result = authRepository.getSignInMethods()) {
                is Result.Success ->
                    _state.update { it.copy(isLoading = false, methods = result.data) }

                is Result.Failure -> {
                    _state.update { it.copy(isLoading = false) }
                    snackbarController.show(result.error.toSnackbarMessage())
                }
            }
        }

    private fun connect(provider: SocialProvider) {
        if (_state.value.busyProvider != null) return
        // Onay (18+ / gizlilik) kayıt anında alınmıştı; bağlama yalnız kimliği kanıtlar.
        _state.update { it.copy(busyProvider = provider) }
        viewModelScope.launch {
            eventChannel.send(SignInMethodsEvent.LaunchProvider(provider))
        }
    }

    private fun handleSignInResult(result: SocialSignInResult) {
        when (result) {
            SocialSignInResult.Cancelled -> _state.update { it.copy(busyProvider = null) }

            SocialSignInResult.Failed -> {
                _state.update { it.copy(busyProvider = null) }
                viewModelScope.launch {
                    snackbarController.show(
                        SnackbarMessage(
                            title = UIText.Resource(Res.string.social_sign_in_failed_title),
                            description = UIText.Resource(Res.string.social_sign_in_failed_description),
                            variant = SnackbarVariant.Error,
                        ),
                    )
                }
            }

            is SocialSignInResult.Success -> link(result)
        }
    }

    private fun link(result: SocialSignInResult.Success) =
        viewModelScope.launch {
            val linkResult =
                authRepository.linkIdentity(
                    LinkIdentityParams(
                        provider = result.provider,
                        idToken = result.idToken,
                        nonce = result.nonce,
                    ),
                )

            when (linkResult) {
                is Result.Success -> {
                    reload()
                    showLinked(result.provider)
                }

                is Result.Failure -> {
                    val error = linkResult.error
                    // Sağlayıcı hesabı başka bir profile bağlıysa snackbar yetmez: kullanıcı
                    // ne olduğunu ve hesapların birleşmediğini gören bir sheet hak ediyor.
                    if (error is DataError.Remote.Business && error.code == IDENTITY_ALREADY_LINKED) {
                        _state.update { it.copy(conflictProvider = result.provider) }
                    } else {
                        snackbarController.show(error.toSnackbarMessage())
                    }
                }
            }

            _state.update { it.copy(busyProvider = null) }
        }

    private fun unlink() {
        val provider = _state.value.unlinkTarget ?: return
        _state.update { it.copy(unlinkTarget = null, busyProvider = provider) }

        viewModelScope.launch {
            when (val result = authRepository.unlinkIdentity(provider)) {
                is Result.Success -> {
                    reload()
                    showUnlinked(provider)
                }

                is Result.Failure -> snackbarController.show(result.error.toSnackbarMessage())
            }

            _state.update { it.copy(busyProvider = null) }
        }
    }

    private fun savePassword(currentPassword: String, newPassword: String) {
        if (_state.value.isSavingPassword) return
        // Sosyal hesaba ilk şifre konurken doğrulanacak mevcut şifre yok.
        val current = currentPassword.takeIf { _state.value.methods?.hasPassword == true }
        _state.update { it.copy(isSavingPassword = true) }

        viewModelScope.launch {
            when (val result = authRepository.changePassword(current, newPassword)) {
                is Result.Success -> {
                    // Backend eski refresh token'ları iptal etti; yeni oturumu hemen sakla.
                    sessionStorage.set(result.data)
                    _state.update { it.copy(isSavingPassword = false, passwordSheet = null) }
                    reload()
                    snackbarController.show(
                        SnackbarMessage(
                            title = UIText.Resource(Res.string.sign_in_methods_password_saved_title),
                            description = UIText.Resource(Res.string.sign_in_methods_password_saved_description),
                            variant = SnackbarVariant.Success,
                        ),
                    )
                }

                is Result.Failure -> {
                    _state.update { it.copy(isSavingPassword = false) }
                    snackbarController.show(result.error.toSnackbarMessage())
                }
            }
        }
    }

    private suspend fun reload() {
        val result = authRepository.getSignInMethods()
        if (result is Result.Success) {
            _state.update { it.copy(methods = result.data) }
        }
    }

    private suspend fun showLinked(provider: SocialProvider) {
        snackbarController.show(
            SnackbarMessage(
                title = UIText.Resource(Res.string.sign_in_methods_linked_title, arrayOf(provider.name)),
                description = UIText.Resource(
                    Res.string.sign_in_methods_linked_description,
                    arrayOf(provider.name),
                ),
                variant = SnackbarVariant.Success,
            ),
        )
    }

    private suspend fun showUnlinked(provider: SocialProvider) {
        snackbarController.show(
            SnackbarMessage(
                title = UIText.Resource(Res.string.sign_in_methods_unlinked_title, arrayOf(provider.name)),
                description = UIText.Resource(Res.string.sign_in_methods_unlinked_description),
                variant = SnackbarVariant.Info,
            ),
        )
    }
}
