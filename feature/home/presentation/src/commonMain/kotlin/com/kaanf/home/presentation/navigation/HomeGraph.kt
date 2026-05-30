package com.kaanf.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.kaanf.home.presentation.dashboard.DashboardRoot
import com.kaanf.home.presentation.dashboard.DashboardScreen
import com.kaanf.home.presentation.eventcode.EventCodeRoot
import com.kaanf.home.presentation.eventdetail.EventDetailRoot
import com.kaanf.home.presentation.gamelobby.GameLobbyRoot
import com.kaanf.home.presentation.ticketqr.TicketQrRoot

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
            EventDetailRoot(
                onBackClick = {
                    navController.popBackStack()
                },
                onCheckoutSuccess = {
                    navController.navigate(HomeGraphRoutes.TicketQr) {
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<HomeGraphRoutes.TicketQr> {
            TicketQrRoot(
                onEventCodeClicked = {
                    navController.navigate(HomeGraphRoutes.EventCode) {
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<HomeGraphRoutes.EventCode> {
            EventCodeRoot(
                onTicketCodeSuccess = {
                    navController.navigate(HomeGraphRoutes.GameLobby) {
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<HomeGraphRoutes.GameLobby> {
            GameLobbyRoot()
        }
    }
}
