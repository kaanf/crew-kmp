package com.kaanf.crew.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavUri
import androidx.navigation.toRoute
import com.kaanf.core.domain.util.Result
import com.kaanf.core.presentation.snackbar.SnackbarController
import com.kaanf.core.presentation.snackbar.toSnackbarMessage
import com.kaanf.home.domain.repository.EventRepository
import com.kaanf.home.presentation.navigation.HomeGraphRoutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun DeepLinkListener(navController: NavController) {
    val events: EventRepository = koinInject()
    val snackbar: SnackbarController = koinInject()
    val scope = rememberCoroutineScope()
    DisposableEffect(navController, events, snackbar, scope) {
        var resolution: Job? = null

        fun isCurrentEvent(eventId: String): Boolean {
            val entry = navController.currentBackStackEntry
            return entry?.destination?.hasRoute<HomeGraphRoutes.EventDetail>() == true &&
                entry.toRoute<HomeGraphRoutes.EventDetail>().eventId.equals(eventId, ignoreCase = true)
        }

        ExternalUriHandler.listener = { uri ->
            resolution?.cancel()
            val code = Regex("https://(?:share\\.)?crewishere\\.com/e/([a-z0-9]{10})/?(?:[?#].*)?")
                .matchEntire(uri)?.groupValues?.get(1)
            if (code != null) {
                resolution = scope.launch {
                    when (val result = events.resolveShareCode(code)) {
                        is Result.Success -> if (!isCurrentEvent(result.data)) {
                            navController.navigate(HomeGraphRoutes.EventDetail(result.data))
                        }
                        is Result.Failure -> snackbar.show(result.error.toSnackbarMessage())
                    }
                }
            } else {
                val navUri = NavUri(uri)
                // Ignore unknown links rather than navigating to a missing destination.
                if (navController.graph.hasDeepLink(navUri)) {
                    val eventId = when {
                        uri.startsWith("crew://event/", ignoreCase = true) ->
                            navUri.getPathSegments().singleOrNull()
                        uri.startsWith("https://api.crewishere.com/share/events/", ignoreCase = true) ->
                            navUri.getPathSegments().lastOrNull()
                        uri.startsWith("https://crewishere.com/", ignoreCase = true) ->
                            navUri.getQueryParameters("id").singleOrNull()
                        else -> null
                    }
                    if (eventId == null || !isCurrentEvent(eventId)) navController.navigate(navUri)
                }
            }
        }

        onDispose {
            resolution?.cancel()
            ExternalUriHandler.listener = null
        }
    }
}
