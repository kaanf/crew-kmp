package com.kaanf.home.presentation.gamelobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.badge.RoundedBadge
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.home.presentation.gamelobby.component.BeforeTheBell
import com.kaanf.home.presentation.gamelobby.component.sampleItems
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GameLobbyRoot(
    viewModel: GameLobbyViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->

    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.EventCode,
                onBackClick = {},
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        GameLobbyScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            state = state,
            onAction = {},
        )
    }
}

@Composable
fun GameLobbyScreen(
    modifier: Modifier = Modifier,
    state: GameLobbyState,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoundedBadge(
                text = "Lobby - Doors Open",
                backgroundColor = AccessDefaults.OnAccent,
                borderColor = AccessDefaults.AccentGlow,
                textColor = AccessDefaults.Accent,
            )

            var checked by remember { mutableStateOf(setOf("drink", "sound")) }

            BeforeTheBell(
                items = sampleItems,
                checkedIds = checked,
                onToggle = { id ->
                    checked = if (id in checked) checked - id else checked + id
                },
            )
        }
    }
}

@Composable
@Preview
fun GameLobbyScreenPreview() {
    CrewTheme {
        GameLobbyScreen(
            state = GameLobbyState(),
            onAction = {}
        )
    }
}
