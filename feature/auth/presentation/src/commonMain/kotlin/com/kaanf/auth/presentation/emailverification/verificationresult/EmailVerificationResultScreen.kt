package com.kaanf.auth.presentation.emailverification.verificationresult

import androidx.compose.foundation.layout.consumeWindowInsets
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
import com.kaanf.core.designsystem.component.layout.LoadingOverlayLayout
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.component.layout.showSnackbar
import com.kaanf.core.presentation.base.BaseEvent
import com.kaanf.core.presentation.util.ObserveAsEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmailVerificationResultRoot(
    viewModel: EmailVerificationResultViewModel = koinViewModel(),
    onLoginClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BaseEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(event.snackbarMessage)
            }

            EmailVerificationResultEvent.NavigateToResult -> {
                onLoginClick.invoke()
            }
        }
    }

    SnackbarScaffold(snackbarHostState = snackbarHostState) { innerPadding ->
        when (state.phase) {
            EmailVerificationPhase.Verifying -> {
                LoadingOverlayLayout(
                    modifier =
                        Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding),
                    isLoading = true,
                ) {}
            }

            EmailVerificationPhase.Failed -> {
            }

            EmailVerificationPhase.Verified -> {
            }
        }
    }
}
