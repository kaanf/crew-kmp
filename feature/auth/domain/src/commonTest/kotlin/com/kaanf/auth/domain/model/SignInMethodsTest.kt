package com.kaanf.auth.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Ekranın tek gerçek kuralı: son giriş yolu kaldırılamaz. Backend de aynı kuralı uygular,
// burada test edilen kullanıcıya "Unlink" mi yoksa "Locked" mı gösterileceği.
class SignInMethodsTest {
    private fun methods(
        hasPassword: Boolean,
        providers: List<SocialProvider>,
    ) = SignInMethods(
        accountEmail = "kaan@crew.app",
        hasPassword = hasPassword,
        signUpProvider = providers.firstOrNull(),
        identities = providers.map { LinkedIdentity(provider = it, email = null) },
    )

    @Test
    fun `the only identity cannot be unlinked when there is no password`() {
        val methods = methods(hasPassword = false, providers = listOf(SocialProvider.Google))

        assertFalse(methods.canUnlink(SocialProvider.Google))
    }

    @Test
    fun `an identity can be unlinked while a password still opens the account`() {
        val methods = methods(hasPassword = true, providers = listOf(SocialProvider.Google))

        assertTrue(methods.canUnlink(SocialProvider.Google))
    }

    @Test
    fun `an identity can be unlinked while another identity remains`() {
        val methods = methods(
            hasPassword = false,
            providers = listOf(SocialProvider.Google, SocialProvider.Apple),
        )

        assertTrue(methods.canUnlink(SocialProvider.Google))
        assertTrue(methods.canUnlink(SocialProvider.Apple))
    }

    @Test
    fun `an unlinked provider is never unlinkable`() {
        val methods = methods(hasPassword = true, providers = listOf(SocialProvider.Google))

        assertFalse(methods.canUnlink(SocialProvider.Apple))
    }

    @Test
    fun `password counts as a method`() {
        assertEquals(2, methods(hasPassword = true, providers = listOf(SocialProvider.Google)).linkedCount)
        assertEquals(1, methods(hasPassword = false, providers = listOf(SocialProvider.Google)).linkedCount)
    }
}
