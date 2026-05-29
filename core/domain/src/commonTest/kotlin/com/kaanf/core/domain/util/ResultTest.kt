package com.kaanf.core.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResultTest {
    @Test
    fun `map transforms success data`() {
        val result = Result.Success(21)

        val mappedResult = result.map { value -> value * 2 }

        assertEquals(Result.Success(42), mappedResult)
    }

    @Test
    fun `map keeps failure unchanged`() {
        val result: Result<Int, TestError> = Result.Failure(TestError.Sample)

        val mappedResult = result.map { value -> value * 2 }

        assertEquals(Result.Failure(TestError.Sample), mappedResult)
    }

    @Test
    fun `on success invokes action only for success result`() {
        val result = Result.Success("classified")
        var actionCalled = false
        var receivedValue: String? = null

        val returnedResult =
            result.onSuccess { value ->
                actionCalled = true
                receivedValue = value
            }

        assertTrue(actionCalled)
        assertEquals("classified", receivedValue)
        assertEquals(result, returnedResult)
    }

    @Test
    fun `on success does not invoke action for failure result`() {
        val result: Result<String, TestError> = Result.Failure(TestError.Sample)
        var actionCalled = false

        val returnedResult =
            result.onSuccess {
                actionCalled = true
            }

        assertFalse(actionCalled)
        assertEquals(Result.Failure(TestError.Sample), returnedResult)
    }

    @Test
    fun `on failure invokes action only for failure result`() {
        val result: Result<String, TestError> = Result.Failure(TestError.Sample)
        var actionCalled = false
        var receivedError: TestError? = null

        val returnedResult =
            result.onFailure { error ->
                actionCalled = true
                receivedError = error
            }

        assertTrue(actionCalled)
        assertEquals(TestError.Sample, receivedError)
        assertEquals(result, returnedResult)
    }

    @Test
    fun `on failure does not invoke action for success result`() {
        val result = Result.Success("classified")
        var actionCalled = false

        val returnedResult =
            result.onFailure {
                actionCalled = true
            }

        assertFalse(actionCalled)
        assertEquals(Result.Success("classified"), returnedResult)
    }

    @Test
    fun `as empty result converts success to unit success`() {
        val result = Result.Success("classified")

        val emptyResult = result.asEmptyResult()

        assertEquals(Result.Success(Unit), emptyResult)
    }

    @Test
    fun `as empty result keeps failure unchanged`() {
        val result: Result<String, TestError> = Result.Failure(TestError.Sample)

        val emptyResult = result.asEmptyResult()

        assertEquals(Result.Failure(TestError.Sample), emptyResult)
    }
}

private enum class TestError : Error {
    Sample,
}
