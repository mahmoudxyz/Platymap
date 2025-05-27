package xyz.mahmoudahmed.dsl.core

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MappingExecutionExceptionTest {

    @Test
    fun `constructor with message should set message`() {
        // When
        val exception = MappingExecutionException("Test message")

        // Then
        assertEquals("Test message", exception.message)
    }

    @Test
    fun `constructor with message and cause should set both`() {
        // Given
        val cause = RuntimeException("Cause message")

        // When
        val exception = MappingExecutionException("Test message", cause)

        // Then
        assertEquals("Test message", exception.message)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `constructor with message and path should format message correctly`() {
        // When
        val exception = MappingExecutionException("Error occurred", "user.address.city")

        // Then
        assertEquals("Error occurred at path: user.address.city", exception.message)
    }
}