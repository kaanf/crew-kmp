package com.kaanf.core.designsystem.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kaanf.core.designsystem.theme.AccessDefaults
import com.kaanf.core.designsystem.theme.CrewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BaseTextField(
    state: TextFieldState,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    hint: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
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
        BaseInputField(
            isFocused = isFocused,
        ) {
            BasicTextField(
                state = state,
                interactionSource = interactionSource,
                textStyle = MaterialTheme.typography.titleSmall.copy(
                    color = AccessDefaults.TextPrimary
                ),
                cursorBrush = SolidColor(Color.White),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                inputTransformation = inputTransformation,
                outputTransformation = outputTransformation,
                lineLimits = TextFieldLineLimits.SingleLine,
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
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp).fillMaxWidth(),
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
                },
            )
        }
    }
}

@Preview
@Composable
private fun BaseTextFieldPreview() {
    CrewTheme(isDarkTheme = true) {
        Column(
            modifier =
                Modifier
                    .background(Color(0xFF0E0B08))
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BaseTextField(
                state = TextFieldState(),
                placeholder = "Email",
            )
            BaseTextField(
                state = TextFieldState("crew@agency.io"),
                placeholder = "Email",
            )
            BasePasswordTextField(
                state = TextFieldState("classified"),
                placeholder = "Password",
            )
        }
    }
}
