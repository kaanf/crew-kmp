package com.kaanf.auth.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.kaanf.auth.presentation.emailverification.verificationresult.EmailVerificationResultRoot
import com.kaanf.auth.presentation.emailverification.verificationsent.EmailVerificationSentRoot
import com.kaanf.auth.presentation.forgotpassword.ForgotPasswordRoot
import com.kaanf.auth.presentation.login.LoginRoot
import com.kaanf.auth.presentation.profilepicture.ProfilePictureRoot
import com.kaanf.auth.presentation.register.RegisterRoot
import com.kaanf.auth.presentation.welcome.WelcomeRoot

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onLoginSuccess: () -> Unit,
) {
    navigation<AuthGraphRoutes.Graph>(
        startDestination = AuthGraphRoutes.Welcome,
    ) {
        composable<AuthGraphRoutes.Welcome> {
            WelcomeRoot(
                onCreateAccountClick = {
                    navController.navigate(AuthGraphRoutes.Register) {
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    navController.navigate(AuthGraphRoutes.Login) {
                        restoreState = true
                        launchSingleTop = true
                    }
                },
            )
        }
        composable<AuthGraphRoutes.Login> {
            LoginRoot(
                onBackClick = {
                    navController.popBackStack()
                },
                onRegisterClick = {
                    navController.navigate(AuthGraphRoutes.Register) {
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(AuthGraphRoutes.ForgotPassword) {
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                onLoginSuccess = onLoginSuccess,
                onProfileIncomplete = {
                    navController.navigate(AuthGraphRoutes.ProfilePicture) {
                        popUpTo(AuthGraphRoutes.Login) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable<AuthGraphRoutes.Register> {
            RegisterRoot(
                onRegisterSuccess = {
                    navController.navigate(
                        AuthGraphRoutes.EmailVerificationSent(it),
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onReturnToLoginClick = {
                    navController.navigate(AuthGraphRoutes.Login) {
                        popUpTo(AuthGraphRoutes.Register) {
                            inclusive = true
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable<AuthGraphRoutes.ProfilePicture> {
            ProfilePictureRoot(
                onBack = {},
                onUploadSuccess = onLoginSuccess,
            )
        }
        composable<AuthGraphRoutes.ForgotPassword> {
            ForgotPasswordRoot()
        }
        composable<AuthGraphRoutes.EmailVerificationSent> {
            EmailVerificationSentRoot(
                onReturnToLoginClick = {
                    navController.popBackStack(AuthGraphRoutes.Login, inclusive = false)
                },
            )
        }
        composable<AuthGraphRoutes.EmailVerificationResult>(
            deepLinks =
                listOf(
                    navDeepLink {
                        this.uriPattern = "https://ads.kaanf.com/api/notification/activate-user?token={token}"
                    },
                    navDeepLink {
                        this.uriPattern = "ads://ads.kaanf.com/api/notification/activate-user?token={token}"
                    },
                ),
        ) {
            EmailVerificationResultRoot(
                onLoginClick = {
                    navController.navigate(AuthGraphRoutes.Login) {
                        popUpTo(AuthGraphRoutes.Register) {
                            inclusive = true
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}
