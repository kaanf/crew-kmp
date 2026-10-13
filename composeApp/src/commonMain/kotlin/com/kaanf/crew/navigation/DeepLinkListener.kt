package com.kaanf.crew.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import androidx.navigation.NavUri

@Composable
fun DeepLinkListener(navController: NavController) {
    DisposableEffect(Unit) {
        ExternalUriHandler.listener = { uri ->
            val navUri = NavUri(uri)
            // Eşleşmeyen link (ör. eski sürüme gelen yeni bir link) navigate'te crash ettirir.
            if (navController.graph.hasDeepLink(navUri)) {
                navController.navigate(navUri)
            }
        }

        onDispose {
            ExternalUriHandler.listener = null
        }
    }
}
