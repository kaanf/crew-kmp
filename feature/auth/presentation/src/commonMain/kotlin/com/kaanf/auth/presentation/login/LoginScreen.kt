package com.kaanf.auth.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.LoadingOverlayLayout
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.component.layout.showSnackbar
import com.kaanf.core.designsystem.component.textfield.BasePasswordTextField
import com.kaanf.core.designsystem.component.textfield.BaseTextField
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.base.BaseEvent
import com.kaanf.core.presentation.model.SnackbarMessage
import com.kaanf.core.presentation.model.SnackbarVariant
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.core.presentation.util.TestTags
import com.kaanf.core.presentation.util.UIText
import com.kaanf.core.presentation.util.clearFocusOnTap
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.login_badge_number_placeholder
import crew.feature.auth.presentation.generated.resources.login_email_label
import crew.feature.auth.presentation.generated.resources.login_passcode_placeholder
import crew.feature.auth.presentation.generated.resources.login_password_label
import crew.feature.auth.presentation.generated.resources.login_primary_action_enter_system
import crew.feature.auth.presentation.generated.resources.login_secondary_action_lost_credentials
import crew.feature.auth.presentation.generated.resources.login_snackbar_success_description
import crew.feature.auth.presentation.generated.resources.login_snackbar_success_title
import crew.feature.auth.presentation.generated.resources.login_subtitle
import crew.feature.auth.presentation.generated.resources.login_title
import crew.feature.auth.presentation.generated.resources.login_top_bar_register_action
import org.jetbrains.compose.resources.getString
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
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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
        }
    }

    SnackbarScaffold(snackbarHostState = snackbarHostState) { innerPadding ->
        LoadingOverlayLayout(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            isLoading = state.isSubmitting,
        ) {
            LoginScreen(
                state = state,
                onAction = viewModel::onAction,
                onBackClick = onBackClick,
            )
        }
    }
}

@Composable
private fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onBackClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clearFocusOnTap()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppTopBar(
                rightText = stringResource(Res.string.login_top_bar_register_action),
                onBackClick = onBackClick,
                onRightClick = { onAction(LoginAction.OnRegisterClick) },
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                        .verticalScroll(scrollState)
            ) {
                Text(
                    text = stringResource(Res.string.login_title),
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = AccessDefaults.TextPrimary
                    )
                )

                Text(
                    text = stringResource(Res.string.login_subtitle),
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
                    BaseTextField(
                        state = state.emailTextState,
                        label = stringResource(Res.string.login_email_label),
                        placeholder = stringResource(Res.string.login_badge_number_placeholder),
                        keyboardType = KeyboardType.Email,
                        testTag = TestTags.LOGIN_EMAIL,
                    )

                    BasePasswordTextField(
                        state = state.passwordTextState,
                        label = stringResource(Res.string.login_password_label),
                        placeholder = stringResource(Res.string.login_passcode_placeholder),
                        testTag = TestTags.LOGIN_PASSWORD,
                        trailing = {
                            Text(
                                text = stringResource(Res.string.login_secondary_action_lost_credentials),
                                modifier =
                                    Modifier
                                        .testTag(TestTags.LOGIN_FORGOT_PASSWORD)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = { onAction(LoginAction.OnForgotPasswordClick) },
                                        ),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = AccessDefaults.Accent,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                        },
                    )
                }
            }
        }
        BaseButton(
            text = stringResource(Res.string.login_primary_action_enter_system),
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
            state =
                LoginState(
                    emailTextState = TextFieldState("crew@agency.io"),
                    passwordTextState = TextFieldState("AccessKey9"),
                ),
            onAction = {},
            onBackClick = {},
        )
    }
}
