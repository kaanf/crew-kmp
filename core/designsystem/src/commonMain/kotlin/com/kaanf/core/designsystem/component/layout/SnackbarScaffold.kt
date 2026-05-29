package com.kaanf.core.designsystem.component.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun SnackbarScaffold(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize(),
    ) {
        Scaffold(
            containerColor = AccessDefaults.Background,
            contentWindowInsets = WindowInsets.safeDrawing
                .exclude(WindowInsets.ime),
        ) { innerPadding ->
            content(innerPadding)
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 12.dp, start = 15.dp, end = 15.dp),
        ) { snackbarData ->
            CustomSnackbar(
                snackbarData = snackbarData,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
            )
        }
    }
}
