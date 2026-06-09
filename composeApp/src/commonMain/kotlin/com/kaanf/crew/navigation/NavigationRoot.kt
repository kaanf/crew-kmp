package com.kaanf.crew.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
        // Slide yerine fade: ekran kaymadığı için pahalı çizimlerin layer cache'i
        // korunur, çift ekran rasterize maliyeti olmaz.
        enterTransition = { fadeIn(tween(150)) },
        exitTransition = { fadeOut(tween(150)) },
        popEnterTransition = { fadeIn(tween(150)) },
        popExitTransition = { fadeOut(tween(150)) },
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
