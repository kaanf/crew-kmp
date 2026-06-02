package com.kaanf.game.presentation.loseraccepts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.model.ChallengeCardUiModel
import com.kaanf.core.presentation.model.ChallengeCardVariant
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.game.presentation.component.WinnerPickChallengeCard
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoserAcceptsRoot(
    viewModel: LoserAcceptsViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            LoserAcceptsEvent.NavigateBack -> onBack()
        }
    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.GameLobby("Loser Accepts"),
                onBackClick = { viewModel.onAction(LoserAcceptsAction.OnBackClick) },
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        LoserAcceptsScreen(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoserAcceptsScreen(
    modifier: Modifier = Modifier,
    state: LoserAcceptsState,
    onAction: (LoserAcceptsAction) -> Unit,
) {
    BackHandler {
        onAction(LoserAcceptsAction.OnBackClick)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "MIRA PICKED THIS FOR YOU",
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp,
            ),
        )

        Text(
            text = "Up to you.",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        WinnerPickChallengeCard(
            card = ChallengeCardUiModel(
                description = "\uD83C\uDF0D Get two strangers to teach you the same word in their language. Both of them, same word.",
                variant = ChallengeCardVariant.Social,
                points = 20,
            ),
            isExpanded = true
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BaseButton(
                text = "Reject",
                filled = false,
                onClick = {},
                modifier = Modifier
                    .weight(0.5f),
            )

            BaseButton(
                text = "Accept",
                filled = true,
                onClick = {},
                modifier = Modifier
                    .weight(0.5f),
            )
        }
    }
}

@Composable
@Preview
fun LoserAcceptsScreenPreview() {
    CrewTheme {
        LoserAcceptsScreen(
            state = LoserAcceptsState(),
            onAction = {}
        )
    }
}
