package com.kaanf.auth.presentation.register

import androidx.compose.foundation.text.input.TextFieldState
import com.kaanf.auth.domain.model.Gender
import com.kaanf.auth.domain.validation.EmailValidator
import com.kaanf.auth.domain.validation.PasswordValidator

data class RegisterState(
    val emailTextState: TextFieldState = TextFieldState(),
    val rePasswordTextState: TextFieldState = TextFieldState(),
    val passwordTextState: TextFieldState = TextFieldState(),
    val fullNameTextState: TextFieldState = TextFieldState(),
    val dateOfBirthTextState: TextFieldState = TextFieldState(),
    val gender: Gender? = null,
    val hasAcceptedTerms: Boolean = false,
    val isRegistering: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val showUnderageDialog: Boolean = false,
) {
    val isEmailValid: Boolean
        get() = EmailValidator.validate(emailTextState.text.toString())

    val isPasswordValid: Boolean
        get() =
            PasswordValidator.validate(passwordTextState.text.toString())
                .isValidPassword

    val isPasswordMatch: Boolean
        get() = passwordTextState.text.toString() == rePasswordTextState.text.toString()

    val isFullNameValid: Boolean
        get() = fullNameTextState.text.trim().length > 3

    val isDateOfBirthValid: Boolean
        get() = dateOfBirthTextState.text.length == DATE_OF_BIRTH_DIGIT_COUNT

    val canSubmit: Boolean
        get() =
            isEmailValid &&
                isPasswordValid &&
                isPasswordMatch &&
                hasAcceptedTerms &&
                isFullNameValid &&
                isDateOfBirthValid
}

private const val DATE_OF_BIRTH_DIGIT_COUNT = 8 // DDMMYYYY, separators are not stored
