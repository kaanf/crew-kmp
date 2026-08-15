package com.kaanf.crew.navigation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kaanf.auth.presentation.navigation.AuthGraphRoutes
import com.kaanf.auth.presentation.navigation.authGraph
import com.kaanf.auth.presentation.signinmethods.SignInMethodsRoot
import com.kaanf.game.presentation.navigation.GameGraphRoutes
import com.kaanf.game.presentation.navigation.gameGraph
import com.kaanf.home.presentation.navigation.HomeGraphRoutes
import com.kaanf.home.presentation.navigation.homeGraph

private const val TRANSITION_DURATION_MILLIS = 350
private const val PARALLAX_NUMERATOR = 3
private const val PARALLAX_DENOMINATOR = 10

private val TransitionEasing = CubicBezierEasing(0.1f, 0.8f, 0.2f, 1f)


@Suppress("FunctionNaming")
@Composable
fun NavigationRoot(
    navController: NavHostController,
    startDestination: Any,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(TRANSITION_DURATION_MILLIS, easing = TransitionEasing),
                initialOffsetX = { fullWidth -> fullWidth },
            )
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(TRANSITION_DURATION_MILLIS, easing = TransitionEasing),
                targetOffsetX = { fullWidth -> -fullWidth * PARALLAX_NUMERATOR / PARALLAX_DENOMINATOR },
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                animationSpec = tween(TRANSITION_DURATION_MILLIS, easing = TransitionEasing),
                initialOffsetX = { fullWidth -> -fullWidth * PARALLAX_NUMERATOR / PARALLAX_DENOMINATOR },
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(TRANSITION_DURATION_MILLIS, easing = TransitionEasing),
                targetOffsetX = { fullWidth -> fullWidth },
            )
        },
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(HomeGraphRoutes.Graph) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
        )

        // Oturum açıkken profilden gelinir; auth grafiğinin giriş akışına ait olmadığı için
        // üst seviyede durur ve geri tuşuyla profile döner.
        composable<AuthGraphRoutes.SignInMethods> {
            SignInMethodsRoot(onBack = { navController.popBackStack() })
        }

        homeGraph(
            navController = navController,
            onSignInMethodsClick = {
                navController.navigate(AuthGraphRoutes.SignInMethods) {
                    launchSingleTop = true
                }
            },
            onGameCodeSuccess = { eventId ->
                navController.navigate(GameGraphRoutes.Graph(eventId = eventId)) {
                    popUpTo(HomeGraphRoutes.Dashboard) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
        )

        gameGraph(
            navController = navController,
            onNavigateToDashboard = {
                navController.popBackStack(
                    route = HomeGraphRoutes.Dashboard,
                    inclusive = false,
                )
            },
        )
    }
}
