package com.kaanf.auth.domain.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmailValidatorTest {
    @Test
    fun `returns true when email is well formed`() {
        assertTrue(EmailValidator.validate("crew.agent@agency.com"))
    }

    @Test
    fun `returns false when at sign is missing`() {
        assertFalse(EmailValidator.validate("crew.agentagency.com"))
    }

    @Test
    fun `returns false when domain suffix is missing`() {
        assertFalse(EmailValidator.validate("crew.agent@agency"))
    }

    @Test
    fun `returns false when domain contains non ascii characters`() {
        assertFalse(EmailValidator.validate("crew.agent@içışğ.com"))
    }

    @Test
    fun `returns false when email contains whitespace`() {
        assertFalse(EmailValidator.validate("crew agent@agency.com"))
    }
}
