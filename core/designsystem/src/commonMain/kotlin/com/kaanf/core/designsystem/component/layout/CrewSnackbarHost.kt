package com.kaanf.core.designsystem.component.layout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CrewSnackbarHost(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier,
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
