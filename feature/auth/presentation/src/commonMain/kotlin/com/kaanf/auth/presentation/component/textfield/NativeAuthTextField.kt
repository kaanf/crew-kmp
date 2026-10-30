package com.kaanf.auth.presentation.component.textfield

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaanf.core.designsystem.component.textfield.BaseInputField
import com.kaanf.core.designsystem.theme.AccessDefaults
import crew.feature.auth.presentation.generated.resources.Res
import crew.feature.auth.presentation.generated.resources.hide_password
import crew.feature.auth.presentation.generated.resources.show_password
import crew.feature.auth.presentation.generated.resources.ic_eye
import crew.feature.auth.presentation.generated.resources.ic_eye_off
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

private const val DATE_DIGIT_COUNT = 8
private const val DATE_SEPARATOR = "-"

internal enum class NativeAuthTextFieldFormat {
    Plain,
    Date,
}

/** Lets a screen move keyboard focus into a native auth field (e.g. on IME "Next"). */
internal class NativeAuthFocusRequester {
    internal var onRequestFocus: () -> Unit = {}

    fun requestFocus() = onRequestFocus()
}

@Composable
internal fun NativeAuthTextField(
    state: TextFieldState,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    hint: String? = null,
    isError: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    format: NativeAuthTextFieldFormat = NativeAuthTextFieldFormat.Plain,
    focusRequester: NativeAuthFocusRequester? = null,
    onImeNext: (() -> Unit)? = null,
    testTag: String? = null,
) {
    NativeAuthFieldLayout(
        modifier = modifier,
        label = label,
        hint = hint,
        isError = isError,
        trailing = trailing,
    ) {
        NativeAuthInputSurface(
            state = state,
            placeholder = placeholder,
            keyboardType = keyboardType,
            format = format,
            isPassword = false,
            focusRequester = focusRequester,
            onImeNext = onImeNext,
            testTag = testTag,
        )
    }
}

@Composable
internal fun NativeAuthPasswordTextField(
    state: TextFieldState,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    hint: String? = null,
    isError: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
    showVisibilityToggle: Boolean = true,
    focusRequester: NativeAuthFocusRequester? = null,
    onImeNext: (() -> Unit)? = null,
    testTag: String? = null,
) {
    NativeAuthFieldLayout(
        modifier = modifier,
        label = label,
        hint = hint,
        isError = isError,
        trailing = trailing,
    ) {
        NativeAuthInputSurface(
            state = state,
            placeholder = placeholder,
            keyboardType = KeyboardType.Password,
            format = NativeAuthTextFieldFormat.Plain,
            isPassword = true,
            showVisibilityToggle = showVisibilityToggle,
            focusRequester = focusRequester,
            onImeNext = onImeNext,
            testTag = testTag,
        )
    }
}

@Composable
private fun NativeAuthInputSurface(
    state: TextFieldState,
    placeholder: String,
    keyboardType: KeyboardType,
    format: NativeAuthTextFieldFormat,
    isPassword: Boolean,
    showVisibilityToggle: Boolean = false,
    focusRequester: NativeAuthFocusRequester? = null,
    onImeNext: (() -> Unit)? = null,
    testTag: String? = null,
) {
    var isFocused by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    // Match native Dynamic Type / Android SP scaling without clipping the input line.
    val fieldHeight = maxOf(52.dp, with(LocalDensity.current) { 20.sp.toDp() } + 32.dp)

    // The native interop view does not let BaseInputField's Compose-drawn background show through
    // on iOS (the field area renders white). Mirror BaseInputField's focus-driven surface color
    // here and paint it on the native view itself so both platforms look identical.
    val containerColor =
        if (isFocused) {
            AccessDefaults.SurfaceHigh
        } else {
            AccessDefaults.SurfaceElevated
        }

    BaseInputField(isFocused = isFocused) {
        if (isPassword && showVisibilityToggle) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(fieldHeight)
                        .padding(start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PlatformNativeAuthTextInput(
                    state = state,
                    placeholder = placeholder,
                    keyboardType = keyboardType,
                    isSecure = !isPasswordVisible,
                    format = format,
                    onFocusChange = { isFocused = it },
                    containerColor = containerColor,
                    focusRequester = focusRequester,
                    onImeNext = onImeNext,
                    testTag = testTag,
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(fieldHeight),
                )

                NativeAuthPasswordVisibilityAction(
                    isPasswordVisible = isPasswordVisible,
                    onClick = { isPasswordVisible = !isPasswordVisible },
                )
            }
        } else {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(fieldHeight)
                        .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                PlatformNativeAuthTextInput(
                    state = state,
                    placeholder = placeholder,
                    keyboardType = keyboardType,
                    isSecure = isPassword,
                    format = format,
                    onFocusChange = { isFocused = it },
                    containerColor = containerColor,
                    focusRequester = focusRequester,
                    onImeNext = onImeNext,
                    testTag = testTag,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
internal fun NativeAuthPasswordVisibilityAction(
    isPasswordVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = vectorResource(if (isPasswordVisible) Res.drawable.ic_eye_off else Res.drawable.ic_eye),
        contentDescription = stringResource(if (isPasswordVisible) Res.string.hide_password else Res.string.show_password),
        tint = AccessDefaults.TextFaint,
        modifier = modifier
            .size(18.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
    )
}

@Composable
private fun NativeAuthFieldLayout(
    modifier: Modifier = Modifier,
    label: String? = null,
    hint: String? = null,
    isError: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
    field: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (label != null || trailing != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = AccessDefaults.TextMuted,
                                fontSize = 12.sp,
                            ),
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                trailing?.invoke()
            }
        }

        field()

        if (hint != null) {
            Text(
                text = hint,
                style =
                    MaterialTheme.typography.bodySmall.copy(
                        color = if (isError) AccessDefaults.Error else AccessDefaults.TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
internal expect fun PlatformNativeAuthTextInput(
    state: TextFieldState,
    placeholder: String,
    keyboardType: KeyboardType,
    isSecure: Boolean,
    format: NativeAuthTextFieldFormat,
    onFocusChange: (Boolean) -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier,
    focusRequester: NativeAuthFocusRequester? = null,
    // null keeps the "Done" key; non-null shows "Next" and runs this instead of dismissing.
    onImeNext: (() -> Unit)? = null,
    testTag: String? = null,
)

internal fun normalizeNativeAuthText(
    format: NativeAuthTextFieldFormat,
    value: String,
): String =
    when (format) {
        NativeAuthTextFieldFormat.Plain -> value
        NativeAuthTextFieldFormat.Date -> value.filter(Char::isDigit).take(DATE_DIGIT_COUNT)
    }

internal fun displayNativeAuthText(
    format: NativeAuthTextFieldFormat,
    rawValue: String,
): String =
    when (format) {
        NativeAuthTextFieldFormat.Plain -> rawValue
        NativeAuthTextFieldFormat.Date -> buildString {
            val digits = rawValue.filter(Char::isDigit).take(DATE_DIGIT_COUNT)
            digits.forEachIndexed { index, char ->
                if (index == 2 || index == 4) {
                    append(DATE_SEPARATOR)
                }
                append(char)
            }
        }
    }
