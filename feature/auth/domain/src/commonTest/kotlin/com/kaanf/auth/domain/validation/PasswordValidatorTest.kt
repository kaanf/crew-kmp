package com.kaanf.auth.domain.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PasswordValidatorTest {
    @Test
    fun `returns false when password is less than 9 characters`() {
        assertFalse(PasswordValidator.validate("121asA2").isValidPassword)
    }

    @Test
    fun `returns false when password is not contains at least one number`() {
        assertFalse(PasswordValidator.validate("Abcdefghi").isValidPassword)
    }

    @Test
    fun `returns false when password is not contains at least one capital letter`() {
        assertFalse(PasswordValidator.validate("abc12defghi").isValidPassword)
    }

    @Test
    fun `returns false when password is empty`() {
        assertFalse(PasswordValidator.validate("").isValidPassword)
    }

    @Test
    fun `returns true when password is valid`() {
        assertTrue(PasswordValidator.validate("Abc123456").isValidPassword)
    }
}
