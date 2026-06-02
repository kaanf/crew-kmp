package com.kaanf.home.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.kaanf.home.presentation.dashboard.DashboardRoot
import com.kaanf.home.presentation.eventcode.EventCodeRoot
import com.kaanf.home.presentation.eventdetail.EventDetailRoot
import com.kaanf.home.presentation.ticketqr.TicketQrRoot

fun NavGraphBuilder.homeGraph(
    navController: NavController,
    onGameCodeSuccess: (eventId: String) -> Unit,
) {
    navigation<HomeGraphRoutes.Graph>(
        startDestination = HomeGraphRoutes.Dashboard,
    ) {
        composable<HomeGraphRoutes.Dashboard> {
            DashboardRoot(
                onEventClicked = { eventId ->
                    navController.navigate(HomeGraphRoutes.EventDetail(eventId)) {
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
                onCheckoutSuccess = { eventId ->
                    navController.navigate(
                        HomeGraphRoutes.TicketQr(eventId = eventId),
                    ) {
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<HomeGraphRoutes.TicketQr> {
            TicketQrRoot(
                onEventCodeClicked = { eventId ->
                    navController.navigate(
                        HomeGraphRoutes.EventCode(
                            eventId = eventId
                        )
                    ) {
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<HomeGraphRoutes.EventCode> { entry ->
            val eventId = entry.toRoute<HomeGraphRoutes.EventCode>().eventId
            EventCodeRoot(
                onTicketCodeSuccess = {
                    onGameCodeSuccess(eventId)
                }
            )
        }
    }
}
