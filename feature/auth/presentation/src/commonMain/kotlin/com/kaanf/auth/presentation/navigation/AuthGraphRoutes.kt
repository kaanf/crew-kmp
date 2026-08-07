package com.kaanf.auth.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface AuthGraphRoutes {
    @Serializable
    data object Graph : AuthGraphRoutes

    @Serializable
    data object Welcome : AuthGraphRoutes

    @Serializable
    data object Login : AuthGraphRoutes

    @Serializable
    data object Register : AuthGraphRoutes

    @Serializable
    data object ProfilePicture : AuthGraphRoutes

    @Serializable
    data class EmailVerificationSent(val email: String) : AuthGraphRoutes

    @Serializable
    data object EmailVerificationResult : AuthGraphRoutes

    @Serializable
    data object ForgotPassword : AuthGraphRoutes

    // Oturum açıkken profilden gelinen ayar ekranı; auth grafiğinin başlangıç akışının parçası
    // değil, NavigationRoot'ta üst seviyede kayıtlı.
    @Serializable
    data object SignInMethods : AuthGraphRoutes
}
