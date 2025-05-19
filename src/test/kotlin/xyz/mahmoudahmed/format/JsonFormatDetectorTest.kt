package xyz.mahmoudahmed.format

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JsonFormatDetectorTest {

    private val detector = JsonFormatDetector()

    @Test
    fun `should return high confidence for valid JSON object`() {
        // Given
        val json = """{"name": "John", "age": 30}""".toByteArray()

        // When
        val confidence = detector.detect(json)

        // Then
        assertTrue(confidence > 0.5f)
    }

    @Test
    fun `should return high confidence for valid JSON array`() {
        // Given
        val json = """[{"name": "John"}, {"name": "Jane"}]""".toByteArray()

        // When
        val confidence = detector.detect(json)

        // Then
        assertTrue(confidence > 0.5f)
    }

    @Test
    fun `should return medium confidence for borderline cases`() {
        // Given
        val json = """{"name": "John" "age": 30}""".toByteArray() // Missing comma

        // When
        val confidence = detector.detect(json)

        // Then
        assertTrue(confidence > 0.0f && confidence < 0.9f)
    }

    @Test
    fun `should return zero confidence for non-JSON`() {
        // Given
        val nonJson = "This is just plain text".toByteArray()

        // When
        val confidence = detector.detect(nonJson)

        // Then
        assertEquals(0.0f, confidence)
    }

    @Test
    fun `should return zero confidence for empty input`() {
        // Given
        val empty = ByteArray(0)

        // When
        val confidence = detector.detect(empty)

        // Then
        assertEquals(0.0f, confidence)
    }

    @Test
    fun `should handle whitespace in JSON`() {
        // Given
        val json = """
            {
                "name": "John",
                "age": 30
            }
        """.trimIndent().toByteArray()

        // When
        val confidence = detector.detect(json)

        // Then
        assertTrue(confidence > 0.5f)
    }
}