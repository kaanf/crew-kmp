package com.kaanf.auth.presentation.component.textfield

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.kaanf.core.designsystem.component.textfield.BasePasswordTextField
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class PasswordVisibilitySemanticsTest {
    @Test
    fun sharedPasswordFieldAnnouncesTheAvailableVisibilityAction() = runComposeUiTest {
        setContent {
            MaterialTheme {
                BasePasswordTextField(
                    state = TextFieldState("example-password"),
                    placeholder = "Password",
                )
            }
        }

        onNodeWithContentDescription("Show Password")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performClick()
        onNodeWithContentDescription("Hide Password").performClick()
        onNodeWithContentDescription("Show Password").assertExists()
    }

    @Test
    fun nativePasswordActionAnnouncesTheAvailableVisibilityAction() = runComposeUiTest {
        setContent {
            MaterialTheme {
                // Test the action used by the native field. UIKitView itself needs the app's
                // interop container and is validated in the platform host, not this Compose host.
                var isVisible by remember { mutableStateOf(false) }
                NativeAuthPasswordVisibilityAction(
                    isPasswordVisible = isVisible,
                    onClick = { isVisible = !isVisible },
                )
            }
        }

        onNodeWithContentDescription("Show Password")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performClick()
        onNodeWithContentDescription("Hide Password").performClick()
        onNodeWithContentDescription("Show Password").assertExists()
    }
}
