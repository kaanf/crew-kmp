package com.kaanf.crew

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.kaanf.auth.presentation.navigation.AuthGraphRoutes
import com.kaanf.core.designsystem.component.layout.CrewSnackbarHost
import com.kaanf.core.designsystem.component.layout.showSnackbar
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.crew.navigation.DeepLinkListener
import com.kaanf.crew.navigation.NavigationRoot
import com.kaanf.home.presentation.navigation.HomeGraphRoutes
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
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

    val snackbarController: SnackbarController = koinInject()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()

    LaunchedEffect(state.isCheckingAuth) {
        if (!state.isCheckingAuth) {
            onAuthenticationChecked()
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is MainEvent.OnSessionExpired -> {
                navController.navigate(AuthGraphRoutes.Graph) {
                    // Wipe the entire home back stack so back can't return into the signed-out app.
                    // Mirrors onSignOut; popUpTo(AuthGraph) was a no-op on cold-start-from-Home.
                    popUpTo(HomeGraphRoutes.Graph) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    ObserveAsEvents(snackbarController.messages) { message ->
        snackbarScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    CrewTheme(isDarkTheme = false) {
        SystemBarsEffect(isDarkTheme = true)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AccessDefaults.Background),
        ) {
            var warmupActive by remember { mutableStateOf(true) }
            if (warmupActive) {
                LaunchedEffect(Unit) {
                    repeat(3) { withFrameNanos { } }
                    warmupActive = false
                }
                ShaderWarmup()
            }

            if (!state.isCheckingAuth) {
                val startDestination = remember {
                    if (state.isLoggedIn) {
                        HomeGraphRoutes.Graph
                    } else {
                        AuthGraphRoutes.Graph
                    }
                }

                NavigationRoot(
                    navController = navController,
                    startDestination = startDestination,
                )
            }

            CrewSnackbarHost(
                snackbarHostState = snackbarHostState,
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 12.dp, start = 15.dp, end = 15.dp),
            )
        }
    }
}
