package com.kaanf.crew

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.kaanf.auth.presentation.navigation.AuthGraphRoutes
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.crew.navigation.DeepLinkListener
import com.kaanf.crew.navigation.NavigationRoot
import com.kaanf.home.presentation.navigation.HomeGraphRoutes
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    viewModel: MainViewModel = koinViewModel(),
    onAuthenticationChecked: () -> Unit = {},
) {
    val navController = rememberNavController()
    val isDarkTheme = isSystemInDarkTheme()
    DeepLinkListener(navController)

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCheckingAuth) {
        if (!state.isCheckingAuth) {
            onAuthenticationChecked()
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is MainEvent.OnSessionExpired -> {
                navController.navigate(AuthGraphRoutes.Graph) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = false
                    }
                }
            }
        }
    }

    CrewTheme(isDarkTheme = false) {
        SystemBarsEffect(isDarkTheme = true)

        if (!state.isCheckingAuth) {
            NavigationRoot(
                navController = navController,
                startDestination = if(state.isLoggedIn) {
                    HomeGraphRoutes.Graph
                } else {
                    AuthGraphRoutes.Graph
                }
            )
        }
    }
}
