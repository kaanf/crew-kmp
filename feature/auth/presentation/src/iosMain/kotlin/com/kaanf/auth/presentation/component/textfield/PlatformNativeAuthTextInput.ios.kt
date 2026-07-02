package com.kaanf.auth.presentation.component.textfield

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import com.kaanf.core.designsystem.theme.AccessDefaults
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSAttributedString
import platform.Foundation.NSSelectorFromString
import platform.Foundation.create
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventEditingChanged
import platform.UIKit.UIFont
import platform.UIKit.UIFontWeightSemibold
import platform.UIKit.UIKeyboardTypeASCIICapable
import platform.UIKit.UIKeyboardTypeDefault
import platform.UIKit.UIKeyboardTypeEmailAddress
import platform.UIKit.UIKeyboardTypeNumberPad
import platform.UIKit.UIKeyboardTypePhonePad
import platform.UIKit.UIReturnKeyType
import platform.UIKit.UITextAutocapitalizationType
import platform.UIKit.UITextAutocorrectionType
import platform.UIKit.UITextBorderStyle
import platform.UIKit.UITextField
import platform.UIKit.UITextFieldDelegateProtocol
import platform.darwin.NSObject

@OptIn(ExperimentalComposeUiApi::class, ExperimentalForeignApi::class, BetaInteropApi::class)
@Suppress("DEPRECATION")
@Composable
internal actual fun PlatformNativeAuthTextInput(
    state: TextFieldState,
    placeholder: String,
    keyboardType: KeyboardType,
    isSecure: Boolean,
    format: NativeAuthTextFieldFormat,
    onFocusChange: (Boolean) -> Unit,
    containerColor: Color,
    modifier: Modifier,
    testTag: String?,
) {
    UIKitView(
        factory = {
            NativeAuthUITextField().apply {
                borderStyle = UITextBorderStyle.UITextBorderStyleNone
                backgroundColor = containerColor.toUIColor()
                opaque = false
                font = UIFont.systemFontOfSize(14.0, UIFontWeightSemibold)
                textColor = AccessDefaults.TextPrimary.toUIColor()
                tintColor = AccessDefaults.Accent.toUIColor()
                autocapitalizationType = UITextAutocapitalizationType.UITextAutocapitalizationTypeNone
                autocorrectionType = UITextAutocorrectionType.UITextAutocorrectionTypeNo
                returnKeyType = UIReturnKeyType.UIReturnKeyDone
                delegate = fieldDelegate
            }
        },
        modifier =
            modifier.then(
                if (testTag != null) {
                    Modifier.testTag(testTag)
                } else {
                    Modifier
                },
            ),
        update = { textField ->
            val displayValue = displayNativeAuthText(format, state.text.toString())
            val textFont = UIFont.systemFontOfSize(14.0, UIFontWeightSemibold)

            textField.fieldDelegate.format = format
            textField.fieldDelegate.onFocusChange = onFocusChange
            textField.fieldDelegate.onValueChange = { nextValue ->
                if (state.text.toString() != nextValue) {
                    state.setTextAndPlaceCursorAtEnd(nextValue)
                }
            }

            textField.backgroundColor = containerColor.toUIColor()
            textField.opaque = false
            textField.font = textFont
            // Match Android's hint color (TextFaint). UITextField's default placeholder is a
            // system gray, so we colour it explicitly via an attributed string.
            textField.attributedPlaceholder =
                NSAttributedString.create(
                    string = placeholder,
                    attributes =
                        mapOf<Any?, Any?>(
                            NSForegroundColorAttributeName to AccessDefaults.TextFaint.toUIColor(),
                        ),
                )
            textField.keyboardType = keyboardType.toUiKeyboardType()
            textField.secureTextEntry = isSecure
            textField.textColor = AccessDefaults.TextPrimary.toUIColor()
            textField.tintColor = AccessDefaults.Accent.toUIColor()

            // Compose -> native sync. Only touches the caret when the value genuinely
            // differs (e.g. external state change), so normal typing keeps the native caret.
            if (textField.text.orEmpty() != displayValue) {
                textField.setTextFromCompose(displayValue)
            }
        },
        background = containerColor,
        interactive = true,
        accessibilityEnabled = true,
    )
}

@OptIn(ExperimentalForeignApi::class)
private class NativeAuthUITextField : UITextField(frame = CGRectZero.readValue()) {
    val fieldDelegate = NativeAuthTextFieldDelegate()

    init {
        delegate = fieldDelegate
        // Observe user edits via the control event instead of intercepting
        // `shouldChangeCharactersInRange`. Intercepting (and returning false to set the text
        // manually) made UIKit reset the selection to index 0 after every keystroke, so
        // characters were typed in reverse. Letting UIKit insert the character natively keeps
        // the caret correct; we only reformat for the date mask.
        addTarget(
            target = fieldDelegate,
            action = NSSelectorFromString("handleEditingChanged:"),
            forControlEvents = UIControlEventEditingChanged,
        )
    }

    // UITextField adds its own internal insets on top of the shared 16dp Compose padding, which
    // pushed the iOS text/placeholder further right than Android (whose EditText uses padding 0).
    // Zero the insets so both platforms start text at the same edge and fill the full height.
    override fun textRectForBounds(bounds: CValue<CGRect>): CValue<CGRect> = bounds

    override fun editingRectForBounds(bounds: CValue<CGRect>): CValue<CGRect> = bounds

    override fun placeholderRectForBounds(bounds: CValue<CGRect>): CValue<CGRect> = bounds
}

@OptIn(ExperimentalForeignApi::class)
private class NativeAuthTextFieldDelegate : NSObject(), UITextFieldDelegateProtocol {
    var format: NativeAuthTextFieldFormat = NativeAuthTextFieldFormat.Plain
    var onValueChange: (String) -> Unit = {}
    var onFocusChange: (Boolean) -> Unit = {}

    @ObjCAction
    fun handleEditingChanged(sender: UITextField) {
        val rawValue = normalizeNativeAuthText(format, sender.text.orEmpty())
        onValueChange(rawValue)

        // For the date mask the displayed string differs from what the user typed
        // (separators inserted, digits capped). Reformatting moves the caret to the end,
        // which is the expected behaviour while filling a date. Plain text never differs,
        // so the caret is left untouched.
        val displayValue = displayNativeAuthText(format, rawValue)
        if (sender.text.orEmpty() != displayValue) {
            sender.setTextFromCompose(displayValue)
        }
    }

    override fun textFieldDidBeginEditing(textField: UITextField) {
        onFocusChange(true)
    }

    override fun textFieldDidEndEditing(textField: UITextField) {
        onFocusChange(false)
    }

    override fun textFieldShouldReturn(textField: UITextField): Boolean {
        textField.resignFirstResponder()
        return true
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun UITextField.setTextFromCompose(value: String) {
    text = value
    val end = endOfDocument
    selectedTextRange = textRangeFromPosition(end, end)
}

private fun KeyboardType.toUiKeyboardType() =
    when (this) {
        KeyboardType.Email -> UIKeyboardTypeEmailAddress
        KeyboardType.Number -> UIKeyboardTypeNumberPad
        KeyboardType.Phone -> UIKeyboardTypePhonePad
        KeyboardType.Password -> UIKeyboardTypeASCIICapable
        else -> UIKeyboardTypeDefault
    }

private fun Color.toUIColor(): UIColor =
    UIColor.colorWithRed(
        red = red.toDouble(),
        green = green.toDouble(),
        blue = blue.toDouble(),
        alpha = alpha.toDouble(),
    )
