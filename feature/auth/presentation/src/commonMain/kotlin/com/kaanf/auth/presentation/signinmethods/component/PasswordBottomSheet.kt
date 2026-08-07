package com.kaanf.auth.presentation.signinmethods.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.auth.domain.validation.PasswordValidator
import com.kaanf.auth.presentation.signinmethods.PasswordSheetMode
import com.kaanf.core.designsystem.component.button.BaseButton
import com.kaanf.core.designsystem.component.sheet.ContainerBottomSheet
import com.kaanf.core.designsystem.component.textfield.BasePasswordTextField
import com.kaanf.core.designsystem.theme.AccessDefaults
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_cancel
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_change_title
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_current_label
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_current_placeholder
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_email_note
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_hint
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_new_label
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_placeholder
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_save
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_set_description
import crew.feature.auth.presentation.generated.resources.sign_in_methods_password_set_title
import org.jetbrains.compose.resources.stringResource

/**
 * Şifre belirleme / değiştirme sheet'i. [PasswordSheetMode.Set] halinde doğrulanacak mevcut
 * şifre yoktur — hesap sosyal girişle açılmıştır ve kullanıcı zaten oturum açmış durumdadır.
 *
 * Prototipteki e-posta alanı burada yok: hesabın e-postası kayıt anında zaten belirlenmiştir,
 * onu değiştirmek doğrulama gerektiren ayrı bir akış.
 */
@Composable
fun PasswordBottomSheet(
    mode: PasswordSheetMode,
    accountEmail: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (currentPassword: String, newPassword: String) -> Unit,
) {
    val currentPasswordState = rememberTextFieldState()
    val newPasswordState = rememberTextFieldState()

    ContainerBottomSheet(onDismiss = onDismiss, dismissible = !isSaving) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(
                    if (mode == PasswordSheetMode.Set) {
                        Res.string.sign_in_methods_password_set_title
                    } else {
                        Res.string.sign_in_methods_password_change_title
                    },
                ),
                style = MaterialTheme.typography.headlineSmall.copy(color = AccessDefaults.TextPrimary),
            )

            Text(
                text = if (mode == PasswordSheetMode.Set) {
                    stringResource(Res.string.sign_in_methods_password_set_description)
                } else {
                    stringResource(Res.string.sign_in_methods_password_email_note, accountEmail)
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AccessDefaults.TextSecondary,
                    fontSize = 13.sp,
                ),
            )

            if (mode == PasswordSheetMode.Change) {
                BasePasswordTextField(
                    state = currentPasswordState,
                    label = stringResource(Res.string.sign_in_methods_password_current_label),
                    placeholder = stringResource(Res.string.sign_in_methods_password_current_placeholder),
                )
            }

            BasePasswordTextField(
                state = newPasswordState,
                label = stringResource(Res.string.sign_in_methods_password_new_label),
                placeholder = stringResource(Res.string.sign_in_methods_password_placeholder),
                hint = stringResource(Res.string.sign_in_methods_password_hint),
            )

            val canSubmit by remember(mode) {
                derivedStateOf {
                    PasswordValidator.validate(newPasswordState.text.toString()).isValidPassword &&
                        (mode == PasswordSheetMode.Set || currentPasswordState.text.isNotBlank())
                }
            }

            BaseButton(
                text = stringResource(Res.string.sign_in_methods_password_save),
                onClick = {
                    onSubmit(
                        currentPasswordState.text.toString(),
                        newPasswordState.text.toString(),
                    )
                },
                enabled = canSubmit && !isSaving,
                isLoading = isSaving,
                filled = true,
            )

            Text(
                text = stringResource(Res.string.sign_in_methods_password_cancel),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !isSaving,
                        onClick = onDismiss,
                    ),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextMuted,
                    fontSize = 12.sp,
                ),
            )
        }
    }
}
