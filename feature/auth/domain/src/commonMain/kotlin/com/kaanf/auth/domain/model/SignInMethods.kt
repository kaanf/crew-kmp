package com.kaanf.auth.domain.model

/** Hesaba bağlı tek bir sosyal kimlik. */
data class LinkedIdentity(
    val provider: SocialProvider,
    /** Sağlayıcıdaki e-posta; eski kayıtlarda boş olabilir. */
    val email: String?,
)

/**
 * Bir hesabın tüm giriş yolları. E-posta + şifre her zaman bir yöntemdir; sosyal girişle
 * açılan hesapta [hasPassword] false olur ve kullanıcı sonradan şifre belirleyebilir.
 */
data class SignInMethods(
    val accountEmail: String,
    val hasPassword: Boolean,
    /** Hesabın açıldığı yöntem; null ise e-posta + şifre. */
    val signUpProvider: SocialProvider?,
    val identities: List<LinkedIdentity>,
) {
    val linkedCount: Int
        get() = identities.size + if (hasPassword) 1 else 0

    fun identityOf(provider: SocialProvider): LinkedIdentity? =
        identities.firstOrNull { it.provider == provider }

    /** Son giriş yolu kaldırılamaz; backend de aynı kuralı uygular. */
    fun canUnlink(provider: SocialProvider): Boolean =
        identityOf(provider) != null && (hasPassword || identities.size > 1)
}
