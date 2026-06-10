package com.kaanf.game.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.kaanf.game.presentation.gamelobby.GameLobbyRoot
import com.kaanf.game.presentation.scanopponent.ScanOpponentRoot
import com.kaanf.game.presentation.session.MatchContainerRoot
import com.kaanf.game.presentation.session.MatchSessionViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.gameGraph(
    navController: NavController,
    onNavigateToDashboard: () -> Unit,
) {
    navigation<GameGraphRoutes.Graph>(
        startDestination = GameGraphRoutes.GameLobby,
    ) {
        composable<GameGraphRoutes.GameLobby> { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry<GameGraphRoutes.Graph>()
            }
            val eventId = graphEntry.toRoute<GameGraphRoutes.Graph>().eventId
            // Lobi de graph-scoped session VM'ini kullanır: soket etkinlik boyunca tek.
            val sessionViewModel: MatchSessionViewModel =
                koinViewModel(viewModelStoreOwner = graphEntry)
            GameLobbyRoot(
                viewModel = sessionViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToGame = {
                    navController.navigate(GameGraphRoutes.Game(eventId = eventId))
                },
            )
        }

        composable<GameGraphRoutes.Game> { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry<GameGraphRoutes.Graph>()
            }
            val eventId = graphEntry.toRoute<GameGraphRoutes.Graph>().eventId
            val sessionViewModel: MatchSessionViewModel =
                koinViewModel(viewModelStoreOwner = graphEntry)

            MatchContainerRoot(
                viewModel = sessionViewModel,
                onNavigateToScanOpponent = {
                    navController.navigate(GameGraphRoutes.ScanOpponent(eventId = eventId))
                },
                onNavigateToDashboard = onNavigateToDashboard,
            )
        }

        composable<GameGraphRoutes.ScanOpponent> { entry ->
            val graphEntry = remember(entry) {
                navController.getBackStackEntry<GameGraphRoutes.Graph>()
            }
            val sessionViewModel: MatchSessionViewModel =
                koinViewModel(viewModelStoreOwner = graphEntry)

            ScanOpponentRoot(
                viewModel = sessionViewModel,
                onClose = { navController.popBackStack() },
            )
        }
    }
}
