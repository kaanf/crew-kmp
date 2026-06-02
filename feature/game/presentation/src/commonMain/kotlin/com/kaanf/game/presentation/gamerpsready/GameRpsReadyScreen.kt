package com.kaanf.game.presentation.gamerpsready

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.kaanf.core.designsystem.component.avatar.AvatarCircle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.component.sheet.TwoOptionBottomSheet
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.game.presentation.component.EmojiStackCard
import com.kaanf.game.presentation.gamelobby.GameLobbyAction
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GameRpsReadyRoot(
    viewModel: GameRpsReadyViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            GameRpsReadyEvent.NavigateBack -> onBack()
        }
    }

    SnackbarScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.GameRpsReady,
                onBackClick = { viewModel.onAction(GameRpsReadyAction.OnBackClick) },
            )
        },
        snackbarHostState = snackbarHostState,
    ) { innerPadding ->
        GameRpsReadyScreen(
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
fun GameRpsReadyScreen(
    modifier: Modifier = Modifier,
    state: GameRpsReadyState,
    onAction: (GameRpsReadyAction) -> Unit,
) {
    BackHandler(enabled = !state.showExitConfirmSheet) {
        onAction(GameRpsReadyAction.OnBackClick)
    }

    if (state.showExitConfirmSheet) {
        TwoOptionBottomSheet(
            title = "Leave this round?",
            description = "Neither of you has thrown yet. Bailing is fine, no score change.",
            confirmButtonText = "Keep playing",
            cancelButtonText = "Leave round",
            onConfirmClicked = { onAction(GameRpsReadyAction.OnExitDismissed) },
            onCancelClicked = { onAction(GameRpsReadyAction.OnBackClick) },
            isDismissable = true,
            showDragHandle = true,
            onDismiss = {
                onAction(GameRpsReadyAction.OnExitDismissed)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = 24.dp,
                alignment = Alignment.CenterHorizontally,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarCircle(
                label = "Y",
                color = AccessDefaults.Rose,
                avatarSize = 78,
                textSize = 30.0,
                borderColor = AccessDefaults.BorderSoft,
                borderSize = 2,
            )

            Text(
                text = "VS.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AccessDefaults.TextMuted,
                    letterSpacing = 3.sp,
                    fontSize = 12.sp,
                ),
            )

            AvatarCircle(
                label = "MK",
                color = AccessDefaults.Teal,
                avatarSize = 78,
                textSize = 30.0,
                borderColor = AccessDefaults.BorderSoft,
                borderSize = 2,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Throw on three.",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "Lock eyes, count it yourselves, play it out, then both tap button and report the winner.",
            style = MaterialTheme.typography.titleSmall.copy(
                color = AccessDefaults.TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        EmojiStackCard(
            size = 84.dp,
            isWaving = false,
        )

        Spacer(modifier = Modifier.height(12.dp))

        BaseButton(
            text = "We played",
            onClick = {},
            filled = true,
            loadingText = "Waiting on Mira..."
        )
    }
}

@Composable
@Preview
fun GameRpsReadyScreenPreview() {
    CrewTheme {
        GameRpsReadyScreen(
            state = GameRpsReadyState(),
            onAction = {}
        )
    }
}
