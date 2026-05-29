package com.kaanf.auth.presentation.emailverification.verificationsent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.component.layout.showSnackbar
import com.kaanf.core.presentation.base.BaseEvent
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.core.presentation.util.TestTags
import crew.feature.auth.presentation.generated.resources.Res
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmailVerificationSentRoot(
    viewModel: EmailVerificationSentViewModel = koinViewModel(),
    onReturnToLoginClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BaseEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(event.snackbarMessage)
            }

            EmailVerificationSentEvent.NavigateToLogin -> {
                onReturnToLoginClick.invoke()
            }
        }
    }

    SnackbarScaffold(snackbarHostState) { innerPadding ->
        EmailVerificationSentScreen(
            modifier =
                Modifier
                    .fillMaxSize()
                    .testTag(TestTags.VERIFICATION_SENT_SCREEN)
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
fun EmailVerificationSentScreen(
    modifier: Modifier = Modifier,
    state: EmailVerificationSentState,
    onAction: (EmailVerificationSentAction) -> Unit,
) {
}
