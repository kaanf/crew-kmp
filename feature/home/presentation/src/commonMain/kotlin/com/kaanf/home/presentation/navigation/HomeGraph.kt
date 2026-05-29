package com.kaanf.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.kaanf.home.presentation.dashboard.DashboardRoot
import com.kaanf.home.presentation.dashboard.DashboardScreen
import com.kaanf.home.presentation.eventdetail.EventDetailRoot

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<HomeGraphRoutes.Graph>(
        startDestination = HomeGraphRoutes.Dashboard,
    ) {
        composable<HomeGraphRoutes.Dashboard> {
            DashboardRoot(
                onEventClicked = {
                    navController.navigate(HomeGraphRoutes.EventDetail) {
                        restoreState = true
                        launchSingleTop = true
                    }
                },
            )
        }

        composable<HomeGraphRoutes.EventDetail> {
            EventDetailRoot()
        }
    }
}
