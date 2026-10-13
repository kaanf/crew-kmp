package com.kaanf.core.designsystem.component.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    containerColor: Color = AccessDefaults.Background,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        containerColor = containerColor,
        contentWindowInsets = WindowInsets.safeDrawing
            .exclude(WindowInsets.ime),
    ) { innerPadding ->
        content(innerPadding)
    }
}
