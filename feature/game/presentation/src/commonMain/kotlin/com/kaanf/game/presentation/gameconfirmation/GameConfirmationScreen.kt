package com.kaanf.game.presentation.gameconfirmation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.game.presentation.whowon.model.OpponentClaimType
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GameConfirmationRoot(
    viewModel: GameConfirmationViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            GameConfirmationEvent.NavigateBack -> onBack()
        }
    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.GameConfirmation,
                onBackClick = { viewModel.onAction(GameConfirmationAction.OnBackClick) },
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        GameConfirmationScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameConfirmationScreen(
    modifier: Modifier = Modifier,
    state: GameConfirmationState,
    onAction: (GameConfirmationAction) -> Unit,
) {
    BackHandler {
        onAction(GameConfirmationAction.OnBackClick)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "CONFIRM THE RESULT",
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp
            ),
        )

        Text(
            text = if (state.claimType == OpponentClaimType.Win) {
                "Did Mira win?"
            } else {
                "Did Mira lost?"
            },
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            text = if (state.claimType == OpponentClaimType.Win) {
                "Mira says they won this round. Confirm if that’s true — then Mira will choose your task."
            } else {
                "Mira says they lost this round. Confirm if that’s true — then you’ll choose their task."
            },
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        BaseButton(
            text = "Confirm",
            filled = true,
            onClick = {}
        )

        BaseButton(
            text = "Cancel match",
            filled = false,
            onClick = {}
        )
    }
}

@Composable
@Preview
fun GameConfirmationScreenPreview() {
    CrewTheme {
        GameConfirmationScreen(
            state = GameConfirmationState(),
            onAction = {}
        )
    }
}
