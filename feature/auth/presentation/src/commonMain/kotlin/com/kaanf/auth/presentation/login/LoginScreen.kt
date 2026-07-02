package com.kaanf.auth.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.auth.presentation.component.textfield.NativeAuthPasswordTextField
import com.kaanf.auth.presentation.component.textfield.NativeAuthTextField
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.model.AppTopBarState
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.core.presentation.util.TestTags
import com.kaanf.core.presentation.util.clearFocusOnTap
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.auth_email_label
import crew.feature.auth.presentation.generated.resources.auth_email_placeholder
import crew.feature.auth.presentation.generated.resources.auth_password_label
import crew.feature.auth.presentation.generated.resources.login_description
import crew.feature.auth.presentation.generated.resources.login_headline
import crew.feature.auth.presentation.generated.resources.login_password_placeholder
import crew.feature.auth.presentation.generated.resources.login_primary_action_sign_in
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginRoot(
    viewModel: LoginViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onProfileIncomplete: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            LoginEvent.NavigateToRegister -> {
                onRegisterClick.invoke()
            }

            LoginEvent.NavigateToForgotPassword -> {
                onForgotPasswordClick.invoke()
            }

            LoginEvent.NavigateToDashboard -> {
                onLoginSuccess.invoke()
            }

            LoginEvent.NavigateToProfilePicture -> {
                onProfileIncomplete.invoke()
            }
        }
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.Login,
                onBackClick = onBackClick,
                onRightClick = { viewModel.onAction(LoginAction.OnRegisterClick) },
            )
        },
    ) { innerPadding ->
        LoginScreen(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
private fun LoginScreen(
    modifier: Modifier,
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .testTag(TestTags.LOGIN_SCREEN)
                .clearFocusOnTap(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                        .verticalScroll(scrollState),
            ) {
                Text(
                    text = stringResource(Res.string.login_headline),
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = AccessDefaults.TextPrimary,
                    ),
                )

                Text(
                    text = stringResource(Res.string.login_description),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AccessDefaults.TextMuted,
                        fontSize = 14.sp,
                    ),
                    modifier = Modifier.padding(top = 8.dp),
                )

                Column(
                    modifier = Modifier.padding(top = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    NativeAuthTextField(
                        state = state.emailTextState,
                        label = stringResource(Res.string.auth_email_label),
                        placeholder = stringResource(Res.string.auth_email_placeholder),
                        keyboardType = KeyboardType.Email,
                        testTag = TestTags.LOGIN_EMAIL,
                    )

                    NativeAuthPasswordTextField(
                        state = state.passwordTextState,
                        label = stringResource(Res.string.auth_password_label),
                        placeholder = stringResource(Res.string.login_password_placeholder),
                        testTag = TestTags.LOGIN_PASSWORD,
                        // ponytail: forgot-password hidden — ForgotPassword destination not registered (crash). Restore when the screen exists.
                    )
                }
            }
        }

        BaseButton(
            text = stringResource(Res.string.login_primary_action_sign_in),
            onClick = {
                focusManager.clearFocus()
                onAction(LoginAction.OnLoginClick)
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.BottomCenter)
                    .testTag(TestTags.LOGIN_SUBMIT),
            isLoading = state.isSubmitting,
            enabled = state.canSubmit,
            filled = true,
        )
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    CrewTheme(isDarkTheme = true) {
        LoginScreen(
            modifier = Modifier,
            state =
                LoginState(
                    emailTextState = TextFieldState("crew@agency.io"),
                    passwordTextState = TextFieldState("AccessKey9"),
                ),
            onAction = {},
        )
    }
}
