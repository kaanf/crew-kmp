package com.kaanf.auth.presentation.signinmethods

import com.kaanf.auth.domain.model.SignInMethods
import com.kaanf.auth.domain.model.SocialProvider

/** Şifre sheet'inin iki hali: sosyal hesaba ilk şifreyi koymak ya da mevcut şifreyi değiştirmek. */
enum class PasswordSheetMode {
    Set,
    Change,
}

data class SignInMethodsState(
    val isLoading: Boolean = true,
    val methods: SignInMethods? = null,
    /** Sağlayıcı akışı ya da link/unlink isteği süren provider. */
    val busyProvider: SocialProvider? = null,
    val unlinkTarget: SocialProvider? = null,
    /** Sağlayıcı hesabı başka bir Crew profiline bağlıysa gösterilecek sheet. */
    val conflictProvider: SocialProvider? = null,
    val passwordSheet: PasswordSheetMode? = null,
    val isSavingPassword: Boolean = false,
)
