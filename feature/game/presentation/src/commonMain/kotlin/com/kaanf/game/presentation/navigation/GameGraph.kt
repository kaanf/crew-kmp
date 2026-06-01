package com.kaanf.game.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.kaanf.game.presentation.game.GameRoot
import com.kaanf.game.presentation.gameconfirmation.GameConfirmationRoot
import com.kaanf.game.presentation.gamerpsready.GameRpsReadyRoot
import com.kaanf.game.presentation.gamelobby.GameLobbyRoot
import com.kaanf.game.presentation.losereveal.LoseRevealRoot
import com.kaanf.game.presentation.personalmatchqr.PersonalMatchQRRoot
import com.kaanf.game.presentation.scanopponent.ScanOpponentRoot
import com.kaanf.game.presentation.whowon.WhoWonRoot
import com.kaanf.game.presentation.winreveal.WinRevealRoot

fun NavGraphBuilder.gameGraph(
    navController: NavController,
    onNavigateToDashboard: () -> Unit,
) {
    navigation<GameGraphRoutes.Graph>(
        startDestination = GameGraphRoutes.GameLobby,
    ) {
        composable<GameGraphRoutes.GameLobby> {
            GameLobbyRoot(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToGame = {
                    navController.navigate(GameGraphRoutes.Game)
                },
            )
        }

        composable<GameGraphRoutes.Game> {
            GameRoot(
                onNavigateToDashboard = onNavigateToDashboard,
                onNavigateScanOpponent = {
                    navController.navigate(GameGraphRoutes.ScanOpponent)
                }
            )
        }

        composable<GameGraphRoutes.PersonalMatchQR> {
            PersonalMatchQRRoot(
                onBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<GameGraphRoutes.ScanOpponent> {
            ScanOpponentRoot(
                onCloseClicked = {
                    navController.popBackStack()
                },
            )
        }

        composable<GameGraphRoutes.GameRpsReady> {
            GameRpsReadyRoot(
                onBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<GameGraphRoutes.WhoWon> {
            WhoWonRoot(
                onBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<GameGraphRoutes.GameConfirmation> {
            GameConfirmationRoot(
                onBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<GameGraphRoutes.WinReveal> {
            WinRevealRoot(
                onBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<GameGraphRoutes.LoseReveal> {
            LoseRevealRoot(
                onBack = {
                    navController.popBackStack()
                },
            )
        }

    }
}
