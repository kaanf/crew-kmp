package com.kaanf.core.designsystem.component.textfield

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import crew.core.designsystem.generated.resources.Res
import crew.core.designsystem.generated.resources.ic_eye
import crew.core.designsystem.generated.resources.ic_eye_off
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BasePasswordTextField(
    state: TextFieldState,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    hint: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    showVisibilityToggle: Boolean = true,
    testTag: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LabeledFieldLayout(
        modifier = modifier,
        label = label,
        hint = hint,
        trailing = trailing,
    ) {
        PasswordFieldSurface(
            state = state,
            placeholder = placeholder,
            interactionSource = interactionSource,
            isFocused = isFocused,
            showVisibilityToggle = showVisibilityToggle,
            testTag = testTag,
        )
    }
}

@Composable
private fun PasswordFieldSurface(
    state: TextFieldState,
    placeholder: String,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isFocused: Boolean = false,
    showVisibilityToggle: Boolean = true,
    testTag: String? = null,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    BaseInputField(
        modifier = modifier,
        isFocused = isFocused,
        enabled = enabled,
    ) {
        BasicSecureTextField(
            state = state,
            enabled = enabled,
            interactionSource = interactionSource,
            textStyle = MaterialTheme.typography.titleSmall.copy(color = AccessDefaults.TextPrimary),
            cursorBrush = SolidColor(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textObfuscationMode = if (isPasswordVisible) TextObfuscationMode.Visible else TextObfuscationMode.Hidden,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(
                        if (testTag != null) {
                            Modifier.testTag(testTag)
                        } else {
                            Modifier
                        },
                    ),
            decorator = { innerTextField ->
                if (showVisibilityToggle) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (state.text.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = AccessDefaults.TextFaint,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                            innerTextField()
                        }

                        Icon(
                            imageVector = vectorResource(if (isPasswordVisible) Res.drawable.ic_eye_off else Res.drawable.ic_eye),
                            contentDescription = null,
                            tint = AccessDefaults.TextFaint,
                            modifier =
                                Modifier
                                    .size(18.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { isPasswordVisible = !isPasswordVisible },
                                    ),
                        )
                    }
                } else {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (state.text.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = AccessDefaults.TextFaint,
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                        innerTextField()
                    }
                }
            },
        )
    }
}
