package com.kaanf.game.presentation.whowon

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
import com.kaanf.core.designsystem.component.button.BaseMiniButton
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.game.presentation.model.GameResultOptionUi
import com.kaanf.game.presentation.whowon.component.WhoWonRow
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WhoWonRoot(
    viewModel: WhoWonViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            WhoWonEvent.NavigateBack -> onBack()
        }
    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.GameLobby("Who Won"),
                onBackClick = { viewModel.onAction(WhoWonAction.OnBackClick) },
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        WhoWonScreen(
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
fun WhoWonScreen(
    modifier: Modifier = Modifier,
    state: WhoWonState,
    onAction: (WhoWonAction) -> Unit,
) {
    BackHandler {
        onAction(WhoWonAction.OnBackClick)
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
            text = "WHO WON?",
            style = MaterialTheme.typography.labelSmall.copy(
                color = AccessDefaults.TextMuted,
                fontSize = 12.sp
            ),
        )

        Text(
            text = "Tell us honestly.",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "Mira will confirm what you tap.\nThe host gets pinged if you disagree.",
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        WhoWonRow(
            GameResultOptionUi(
                title = "I won",
                pointText = "+10 PTS",
                description = "You pick a task for Mira",
                emoji = "\uD83D\uDC51",
            ),
        )

        WhoWonRow(
            GameResultOptionUi(
                title = "I lost",
                pointText = "NO BONUS",
                description = "Mira picks a task for you",
                emoji = "\uD83D\uDE05",
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        BaseMiniButton(
            text = "Dispute · ask the host",
            filled = false,
            onClick = {}
        )
    }
}

@Composable
@Preview
fun WhoWonScreenPreview() {
    CrewTheme {
        WhoWonScreen(
            state = WhoWonState(),
            onAction = {}
        )
    }
}
