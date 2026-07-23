package com.kaanf.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanf.auth.domain.model.Gender
import com.kaanf.auth.presentation.component.textfield.NativeAuthPasswordTextField
import com.kaanf.auth.presentation.component.textfield.NativeAuthTextField
import com.kaanf.auth.presentation.component.textfield.NativeAuthTextFieldFormat
import com.kaanf.auth.presentation.util.PolicyUrls
import com.kaanf.auth.presentation.util.appendPolicyLink
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.checkbox.BaseCheckbox
import com.kaanf.core.designsystem.component.layout.AppScaffold
import com.kaanf.core.designsystem.component.layout.AppTopBar
import com.kaanf.core.designsystem.component.dialog.BaseDialog
import com.kaanf.core.designsystem.component.sheet.SelectionBottomSheet
import com.kaanf.core.designsystem.component.textfield.BaseSelectField
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
import crew.feature.auth.presentation.generated.resources.error_invalid_date_of_birth
import crew.feature.auth.presentation.generated.resources.error_invalid_email
import crew.feature.auth.presentation.generated.resources.error_password_mismatch
import crew.feature.auth.presentation.generated.resources.register_confirm_password_label
import crew.feature.auth.presentation.generated.resources.register_confirm_password_placeholder
import crew.feature.auth.presentation.generated.resources.register_date_of_birth_label
import crew.feature.auth.presentation.generated.resources.register_date_of_birth_placeholder
import crew.feature.auth.presentation.generated.resources.register_description
import crew.feature.auth.presentation.generated.resources.register_full_name_label
import crew.feature.auth.presentation.generated.resources.register_full_name_hint
import crew.feature.auth.presentation.generated.resources.register_full_name_placeholder
import crew.feature.auth.presentation.generated.resources.register_gender_female
import crew.feature.auth.presentation.generated.resources.register_gender_label
import crew.feature.auth.presentation.generated.resources.register_gender_male
import crew.feature.auth.presentation.generated.resources.register_gender_non_binary
import crew.feature.auth.presentation.generated.resources.register_gender_other
import crew.feature.auth.presentation.generated.resources.register_gender_placeholder
import crew.feature.auth.presentation.generated.resources.register_gender_prefer_not_to_say
import crew.feature.auth.presentation.generated.resources.register_gender_sheet_title
import crew.feature.auth.presentation.generated.resources.register_headline
import crew.feature.auth.presentation.generated.resources.register_password_placeholder
import crew.feature.auth.presentation.generated.resources.register_password_requirements_hint
import crew.feature.auth.presentation.generated.resources.register_primary_action_create_account
import crew.feature.auth.presentation.generated.resources.register_house_rules
import crew.feature.auth.presentation.generated.resources.register_privacy_policy
import crew.feature.auth.presentation.generated.resources.register_terms
import crew.feature.auth.presentation.generated.resources.register_terms_conjunction
import crew.feature.auth.presentation.generated.resources.register_terms_prefix
import crew.feature.auth.presentation.generated.resources.register_terms_separator
import crew.feature.auth.presentation.generated.resources.register_terms_suffix
import crew.feature.auth.presentation.generated.resources.register_underage_description
import crew.feature.auth.presentation.generated.resources.register_underage_go_back
import crew.feature.auth.presentation.generated.resources.register_underage_title
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

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is RegisterEvent.RegisterSuccess -> {
                onRegisterSuccess(event.email)
            }

            is RegisterEvent.NavigateToLogin -> {
                onReturnToLoginClick.invoke()
            }

            is RegisterEvent.NavigateBack -> {
                onBackClick()
            }
        }
    }

    AppScaffold(
        topBar = {
            AppTopBar(
                state = AppTopBarState.Register,
                onBackClick = onBackClick,
                onRightClick = onReturnToLoginClick,
            )
        },
    ) { innerPadding ->
        RegisterScreen(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    var showGenderSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag(TestTags.REGISTER_SCREEN)
            .clearFocusOnTap(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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
                    text = stringResource(Res.string.register_headline),
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = AccessDefaults.TextPrimary,
                    ),
                )

                Text(
                    text = stringResource(Res.string.register_description),
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
                    val showEmailError = state.emailTextState.text.isNotEmpty() && !state.isEmailValid
                    NativeAuthTextField(
                        state = state.emailTextState,
                        label = stringResource(Res.string.auth_email_label),
                        hint = if (showEmailError) stringResource(Res.string.error_invalid_email) else null,
                        isError = showEmailError,
                        placeholder = stringResource(Res.string.auth_email_placeholder),
                        keyboardType = KeyboardType.Email,
                        testTag = TestTags.REGISTER_EMAIL,
                    )

                    NativeAuthPasswordTextField(
                        state = state.passwordTextState,
                        label = stringResource(Res.string.auth_password_label),
                        hint = stringResource(Res.string.register_password_requirements_hint),
                        isError = state.passwordTextState.text.isNotEmpty() && !state.isPasswordValid,
                        placeholder = stringResource(Res.string.register_password_placeholder),
                        testTag = TestTags.REGISTER_PASSWORD,
                    )

                    val showPasswordMismatch =
                        state.rePasswordTextState.text.isNotEmpty() && !state.isPasswordMatch
                    NativeAuthPasswordTextField(
                        state = state.rePasswordTextState,
                        label = stringResource(Res.string.register_confirm_password_label),
                        hint = if (showPasswordMismatch) stringResource(Res.string.error_password_mismatch) else null,
                        isError = showPasswordMismatch,
                        placeholder = stringResource(Res.string.register_confirm_password_placeholder),
                        testTag = TestTags.REGISTER_RE_PASSWORD,
                    )

                    NativeAuthTextField(
                        state = state.fullNameTextState,
                        label = stringResource(Res.string.register_full_name_label),
                        hint = stringResource(Res.string.register_full_name_hint),
                        isError = state.fullNameTextState.text.isNotEmpty() && !state.isFullNameValid,
                        placeholder = stringResource(Res.string.register_full_name_placeholder),
                    )

                    val showDateOfBirthError =
                        state.dateOfBirthTextState.text.isNotEmpty() && !state.isDateOfBirthValid
                    NativeAuthTextField(
                        state = state.dateOfBirthTextState,
                        label = stringResource(Res.string.register_date_of_birth_label),
                        hint = if (showDateOfBirthError) stringResource(Res.string.error_invalid_date_of_birth) else null,
                        isError = showDateOfBirthError,
                        placeholder = stringResource(Res.string.register_date_of_birth_placeholder),
                        keyboardType = KeyboardType.Number,
                        format = NativeAuthTextFieldFormat.Date,
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
                        label = buildAnnotatedString {
                            append(stringResource(Res.string.register_terms_prefix))
                            appendPolicyLink(
                                text = stringResource(Res.string.register_terms),
                                url = PolicyUrls.TERMS_OF_USE,
                            )
                            append(stringResource(Res.string.register_terms_separator))
                            appendPolicyLink(
                                text = stringResource(Res.string.register_house_rules),
                                url = PolicyUrls.HOUSE_RULES,
                            )
                            append(stringResource(Res.string.register_terms_conjunction))
                            appendPolicyLink(
                                text = stringResource(Res.string.register_privacy_policy),
                                url = PolicyUrls.PRIVACY_POLICY,
                            )
                            append(stringResource(Res.string.register_terms_suffix))
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .testTag(TestTags.REGISTER_TERMS),
                    )

                    BaseButton(
                        text = stringResource(Res.string.register_primary_action_create_account),
                        onClick = {
                            focusManager.clearFocus()
                            onAction(RegisterAction.OnRegisterClick)
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 20.dp)
                                .testTag(TestTags.REGISTER_SUBMIT),
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

        if (state.showUnderageDialog) {
            UnderageDialog(
                onGoBack = { onAction(RegisterAction.OnUnderageGoBack) },
            )
        }
    }
}

@Composable
private fun UnderageDialog(onGoBack: () -> Unit) {
    BaseDialog(
        onDismissRequest = onGoBack,
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
    ) {
        Text(
            text = stringResource(Res.string.register_underage_title),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AccessDefaults.TextPrimary,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(Res.string.register_underage_description),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall.copy(
                color = AccessDefaults.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = Modifier.height(18.dp))

        BaseButton(
            text = stringResource(Res.string.register_underage_go_back),
            onClick = onGoBack,
            filled = true,
        )
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
        )
    }
}
