package com.kaanf.auth.presentation.register

import androidx.compose.foundation.BorderStroke
import com.kaanf.auth.domain.model.Gender
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.checkbox.BaseCheckbox
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.layout.LoadingOverlayLayout
import com.kaanf.core.designsystem.component.layout.SnackbarScaffold
import com.kaanf.core.designsystem.component.layout.showSnackbar
import com.kaanf.core.designsystem.component.sheet.SelectionBottomSheet
import com.kaanf.core.designsystem.component.textfield.BasePasswordTextField
import com.kaanf.core.designsystem.component.textfield.BaseSelectField
import com.kaanf.core.designsystem.component.textfield.BaseTextField
import com.kaanf.core.designsystem.component.textfield.DateInputTransformation
import com.kaanf.core.designsystem.component.textfield.DateOutputTransformation
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import com.kaanf.core.presentation.base.BaseEvent
import com.kaanf.core.presentation.util.ObserveAsEvents
import com.kaanf.core.presentation.util.TestTags
import com.kaanf.core.presentation.util.clearFocusOnTap
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.login_email_label
import crew.feature.auth.presentation.generated.resources.login_password_label
import crew.feature.auth.presentation.generated.resources.register_date_of_birth_label
import crew.feature.auth.presentation.generated.resources.register_date_of_birth_placeholder
import crew.feature.auth.presentation.generated.resources.register_email_placeholder
import crew.feature.auth.presentation.generated.resources.register_full_name_label
import crew.feature.auth.presentation.generated.resources.register_full_name_placeholder
import crew.feature.auth.presentation.generated.resources.register_full_name_supporting_text
import crew.feature.auth.presentation.generated.resources.register_gender_female
import crew.feature.auth.presentation.generated.resources.register_gender_label
import crew.feature.auth.presentation.generated.resources.register_gender_male
import crew.feature.auth.presentation.generated.resources.register_gender_non_binary
import crew.feature.auth.presentation.generated.resources.register_gender_other
import crew.feature.auth.presentation.generated.resources.register_gender_placeholder
import crew.feature.auth.presentation.generated.resources.register_gender_prefer_not_to_say
import crew.feature.auth.presentation.generated.resources.register_gender_sheet_title
import crew.feature.auth.presentation.generated.resources.register_password_placeholder
import crew.feature.auth.presentation.generated.resources.register_password_supporting_text
import crew.feature.auth.presentation.generated.resources.register_primary_action_begin_first_case
import crew.feature.auth.presentation.generated.resources.register_re_type_password_label
import crew.feature.auth.presentation.generated.resources.register_re_type_password_placeholder
import crew.feature.auth.presentation.generated.resources.register_subtitle
import crew.feature.auth.presentation.generated.resources.register_terms_agreement
import crew.feature.auth.presentation.generated.resources.register_title
import crew.feature.auth.presentation.generated.resources.register_top_bar_login_action
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterRoot(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: (String) -> Unit,
    onBackClick: () -> Unit,
    onReturnToLoginClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BaseEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(event.snackbarMessage)
            }

            is RegisterEvent.RegisterSuccess -> {
                onRegisterSuccess(event.email)
            }

            is RegisterEvent.NavigateToLogin -> {
                onReturnToLoginClick.invoke()
            }
        }
    }

    SnackbarScaffold(snackbarHostState = snackbarHostState) { innerPadding ->
        LoadingOverlayLayout(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            isLoading = state.isRegistering,
        ) {
            RegisterScreen(
                state = state,
                onAction = viewModel::onAction,
                onBackClick = onBackClick,
                onReturnToLoginClick = onReturnToLoginClick,
            )
        }
    }
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    onBackClick: () -> Unit,
    onReturnToLoginClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    var showGenderSheet by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clearFocusOnTap(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppTopBar(
                rightText = stringResource(Res.string.register_top_bar_login_action),
                onBackClick = onBackClick,
                onRightClick = onReturnToLoginClick,
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                        .verticalScroll(scrollState),
            ) {
                Text(
                    text = stringResource(Res.string.register_title),
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = AccessDefaults.TextPrimary,
                    ),
                )

                Text(
                    text = stringResource(Res.string.register_subtitle),
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
                        placeholder = stringResource(Res.string.register_email_placeholder),
                        keyboardType = KeyboardType.Email,
                    )

                    BasePasswordTextField(
                        state = state.passwordTextState,
                        label = stringResource(Res.string.login_password_label),
                        hint = stringResource(Res.string.register_password_supporting_text),
                        placeholder = stringResource(Res.string.register_password_placeholder),
                    )

                    BasePasswordTextField(
                        state = state.rePasswordTextState,
                        label = stringResource(Res.string.register_re_type_password_label),
                        placeholder = stringResource(Res.string.register_re_type_password_placeholder),
                    )

                    BaseTextField(
                        state = state.fullNameTextState,
                        label = stringResource(Res.string.register_full_name_label),
                        hint = stringResource(Res.string.register_full_name_supporting_text),
                        placeholder = stringResource(Res.string.register_full_name_placeholder),
                    )

                    BaseTextField(
                        state = state.dateOfBirthTextState,
                        label = stringResource(Res.string.register_date_of_birth_label),
                        placeholder = stringResource(Res.string.register_date_of_birth_placeholder),
                        keyboardType = KeyboardType.Number,
                        inputTransformation = DateInputTransformation,
                        outputTransformation = DateOutputTransformation,
                    )

                    BaseSelectField(
                        value = state.gender?.let { stringResource(it.labelRes()) },
                        label = stringResource(Res.string.register_gender_label),
                        placeholder = stringResource(Res.string.register_gender_placeholder),
                        onClick = {
                            focusManager.clearFocus()
                            showGenderSheet = true
                        },
                    )

                    BaseCheckbox(
                        checked = state.hasAcceptedTerms,
                        onCheckedChange = { onAction(RegisterAction.OnTermsToggle) },
                        label = stringResource(Res.string.register_terms_agreement),
                        modifier = Modifier.padding(top = 8.dp),
                    )

                    BaseButton(
                        text = stringResource(Res.string.register_primary_action_begin_first_case),
                        onClick = {
                            focusManager.clearFocus()
                            onAction(RegisterAction.OnRegisterClick)
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 20.dp),
                        isLoading = state.isRegistering,
                        enabled = state.canSubmit,
                        filled = true,
                    )
                }
            }
        }

        if (showGenderSheet) {
            SelectionBottomSheet(
                title = stringResource(Res.string.register_gender_sheet_title),
                options = Gender.entries,
                selected = state.gender,
                labelOf = { stringResource(it.labelRes()) },
                onSelect = { gender ->
                    onAction(RegisterAction.OnGenderSelect(gender))
                    showGenderSheet = false
                },
                onDismiss = { showGenderSheet = false },
            )
        }
    }
}

private fun Gender.labelRes(): StringResource =
    when (this) {
        Gender.Female -> Res.string.register_gender_female
        Gender.Male -> Res.string.register_gender_male
        Gender.NonBinary -> Res.string.register_gender_non_binary
        Gender.Other -> Res.string.register_gender_other
        Gender.PreferNotToSay -> Res.string.register_gender_prefer_not_to_say
    }

@Preview
@Composable
private fun RegisterScreenPreview() {
    CrewTheme(isDarkTheme = true) {
        RegisterScreen(
            state =
                RegisterState(
                    emailTextState = TextFieldState("crew@agency.io"),
                    passwordTextState = TextFieldState("AccessKey9"),
                    rePasswordTextState = TextFieldState("AccessKey9"),
                    hasAcceptedTerms = true,
                ),
            onAction = {},
            onBackClick = {},
            onReturnToLoginClick = {},
        )
    }
}
