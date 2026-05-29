package com.kaanf.core.designsystem.component.textfield

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.ui.text.input.KeyboardType

private const val DATE_DIGIT_COUNT = 8 // DDMMYYYY
private const val DATE_SEPARATOR = "-"

/**
 * Keeps only digits in the [TextFieldState] and caps them at [DATE_DIGIT_COUNT] (DDMMYYYY).
 * The raw state therefore stays separator-free (e.g. `27052000`); [DateOutputTransformation]
 * is responsible for the visual `DD-MM-YYYY` formatting.
 */
val DateInputTransformation: InputTransformation =
    object : InputTransformation {
        override val keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

        override fun TextFieldBuffer.transformInput() {
            val current = asCharSequence()
            val digits = current.filter(Char::isDigit).take(DATE_DIGIT_COUNT)
            if (!current.contentEquals(digits)) {
                replace(0, length, digits)
            }
        }
    }

/**
 * Renders the separator-free date digits as `DD-MM-YYYY` without storing the separators in
 * the backing [TextFieldState]. Separators are inserted from the highest index first so the
 * earlier insertion positions are not shifted.
 */
val DateOutputTransformation: OutputTransformation =
    object : OutputTransformation {
        override fun TextFieldBuffer.transformOutput() {
            val digits = length
            // Insert from the highest index first so earlier positions are not shifted.
            if (digits > 4) replace(4, 4, DATE_SEPARATOR)
            if (digits > 2) replace(2, 2, DATE_SEPARATOR)
        }
    }
