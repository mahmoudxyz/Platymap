package xyz.mahmoudahmed.dsl.core

import org.junit.jupiter.api.Test
import xyz.mahmoudahmed.dsl.builders.SourceBuilder
import xyz.mahmoudahmed.dsl.typed.TypedSourceBuilder
import xyz.mahmoudahmed.dsl.functions.FunctionBuilder
import xyz.mahmoudahmed.logging.LogLevel
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PlatymapTest {

    @Test
    fun `flow with string should return SourceBuilder`() {
        // When
        val builder = Platymap.flow("source")

        // Then
        assertNotNull(builder)
        assert(builder is SourceBuilder)
    }

    @Test
    fun `flow with class should return TypedSourceBuilder`() {
        // Given
        class TestClass

        // When
        val builder = Platymap.flow(TestClass::class.java)

        // Then
        assertNotNull(builder)
        assert(builder is TypedSourceBuilder<*>)
    }

    @Test
    fun `function should return FunctionBuilder`() {
        // When
        val builder = Platymap.function("testFunction")

        // Then
        assertNotNull(builder)
        assert(builder is FunctionBuilder)
        assertEquals("testFunction", builder.name)
    }

    @Test
    fun `getAdapterService should return InputAdapterService`() {
        // When
        val adapterService = Platymap.getAdapterService()

        // Then
        assertNotNull(adapterService)
    }

    @Test
    fun `configureLogging should update global logging config`() {
        // Given - initial state
        val initialMinLevel = xyz.mahmoudahmed.logging.LoggingConfig.minLevel

        // When
        Platymap.configureLogging{
            level(LogLevel.DEBUG)
            logToConsole()
        }

        // Then
        assertEquals(LogLevel.DEBUG, xyz.mahmoudahmed.logging.LoggingConfig.minLevel)

        // Cleanup
        xyz.mahmoudahmed.logging.LoggingConfig.minLevel = initialMinLevel
    }
}