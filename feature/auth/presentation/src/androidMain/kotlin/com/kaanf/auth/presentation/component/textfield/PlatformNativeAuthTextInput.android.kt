package com.kaanf.auth.presentation.component.textfield

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.AndroidView
import com.kaanf.core.designsystem.theme.AccessDefaults

@Composable
internal actual fun PlatformNativeAuthTextInput(
    state: TextFieldState,
    placeholder: String,
    keyboardType: KeyboardType,
    isSecure: Boolean,
    format: NativeAuthTextFieldFormat,
    onFocusChange: (Boolean) -> Unit,
    containerColor: ComposeColor,
    modifier: Modifier,
    testTag: String?,
) {
    AndroidView(
        factory = { context ->
            NativeAuthEditText(context).apply {
                // Static styling is applied once. It must NOT live in `update`: that block
                // re-runs on every recomposition (including the one each keystroke triggers),
                // and some EditText setters reset the caret as a side effect.
                setTextColor(AccessDefaults.TextPrimary.toArgb())
                setHintTextColor(AccessDefaults.TextFaint.toArgb())
                highlightColor = AccessDefaults.Accent.copy(alpha = 0.28f).toArgb()
                background = null
                setBackgroundColor(Color.TRANSPARENT)
                includeFontPadding = false
                gravity = Gravity.CENTER_VERTICAL or Gravity.START
                isSingleLine = true
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                typeface = nativeTextTypeface()
                setPadding(0, 0, 0, 0)
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
        update = { view ->
            val displayValue = displayNativeAuthText(format, state.text.toString())

            view.watcher.format = format
            view.watcher.onValueChange = { nextValue ->
                if (state.text.toString() != nextValue) {
                    state.setTextAndPlaceCursorAtEnd(nextValue)
                }
            }

            view.hint = placeholder
            // Focus-driven surface color (matches BaseInputField). setBackgroundColor is caret-safe,
            // unlike setInputType/setTransformationMethod below.
            view.setBackgroundColor(containerColor.toArgb())

            // setInputType / setTransformationMethod move the caret back to index 0 every time
            // they are called, even with an unchanged value. Since `update` runs on each
            // keystroke, assigning them unconditionally pinned the caret at the start (text was
            // typed in reverse). Only touch them when the value actually changes.
            val nextInputType = keyboardType.toAndroidInputType()
            if (view.inputType != nextInputType) {
                view.inputType = nextInputType
            }

            val nextTransformation =
                if (isSecure) {
                    PasswordTransformationMethod.getInstance()
                } else {
                    null
                }
            if (view.transformationMethod != nextTransformation) {
                view.transformationMethod = nextTransformation
            }

            view.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    onFocusChange(hasFocus)
                }

            if (view.text.toString() != displayValue) {
                view.watcher.replaceFromCompose(displayValue)
            }
        },
    )
}

private class NativeAuthEditText(context: Context) : EditText(context) {
    val watcher = NativeAuthTextWatcher(this)

    init {
        background = null
        setBackgroundColor(Color.TRANSPARENT)
        minHeight = 0
        minimumHeight = 0
        setMinHeight(0)
        addTextChangedListener(watcher)
    }
}

private class NativeAuthTextWatcher(
    private val editText: EditText,
) : TextWatcher {
    var format: NativeAuthTextFieldFormat = NativeAuthTextFieldFormat.Plain
    var onValueChange: (String) -> Unit = {}

    private var isProgrammaticChange = false

    override fun beforeTextChanged(
        text: CharSequence?,
        start: Int,
        count: Int,
        after: Int,
    ) = Unit

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        before: Int,
        count: Int,
    ) = Unit

    override fun afterTextChanged(editable: Editable?) {
        if (isProgrammaticChange) {
            return
        }

        val rawValue = normalizeNativeAuthText(format, editable?.toString().orEmpty())
        val displayValue = displayNativeAuthText(format, rawValue)

        onValueChange(rawValue)

        if (editable?.toString() != displayValue) {
            replaceFromCompose(displayValue)
        }
    }

    fun replaceFromCompose(value: String) {
        isProgrammaticChange = true
        editText.setText(value)
        editText.setSelectionSafely(value.length)
        isProgrammaticChange = false
    }
}

private fun EditText.setSelectionSafely(index: Int) {
    val safeIndex = index.coerceIn(0, text?.length ?: 0)
    setSelection(safeIndex)
}

private fun nativeTextTypeface(): Typeface =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Typeface.create(Typeface.DEFAULT, 600, false)
    } else {
        Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

private fun KeyboardType.toAndroidInputType(): Int =
    when (this) {
        KeyboardType.Email -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        KeyboardType.Number -> InputType.TYPE_CLASS_NUMBER
        KeyboardType.Phone -> InputType.TYPE_CLASS_PHONE
        KeyboardType.Password -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        else -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
    }
