package com.kaanf.crew.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kaanf.auth.presentation.navigation.AuthGraphRoutes
import com.kaanf.auth.presentation.navigation.authGraph
import com.kaanf.game.presentation.navigation.GameGraphRoutes
import com.kaanf.game.presentation.navigation.gameGraph
import com.kaanf.home.presentation.navigation.HomeGraphRoutes
import com.kaanf.home.presentation.navigation.homeGraph

@Suppress("FunctionNaming")
@Composable
fun NavigationRoot(
    navController: NavHostController,
    startDestination: Any
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300),
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300),
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300),
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300),
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

        homeGraph(
            navController = navController,
            onGameCodeSuccess = { eventId ->
                navController.navigate(GameGraphRoutes.Graph(eventId = eventId)) {
                    popUpTo(HomeGraphRoutes.Dashboard) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
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
