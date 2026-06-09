package com.kaanf.auth.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
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
                    navController.navigate(AuthGraphRoutes.Login) {
                        popUpTo(AuthGraphRoutes.Register) {
                            inclusive = true
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
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
                onUploadSuccess = onLoginSuccess,
                onSkip = onLoginSuccess,
            )
        }
    }
}
