package xyz.mahmoudahmed.format;

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CsvFormatDetectorTest {

    private val detector = CsvFormatDetector()

    @Test
    fun `should return high confidence for valid CSV with multiple rows`() {
        // Given
        val csv = """
                id,first_name,last_name,email,department,salary
                1,John,Doe,john.doe@example.com,Engineering,75000
                2,Jane,Smith,jane.smith@example.com,Marketing,65000
                3,Bob,Johnson,bob.johnson@example.com,Sales,60000
                4,Alice,Brown,alice.brown@example.com,Human Resources,70000
                5,Charlie,Davis,charlie.davis@example.com,Engineering,80000
        """.trimIndent().toByteArray()

        // When
        val confidence = detector.detect(csv)

        // Then
        assertTrue(confidence > 0.7f)
    }

    @Test
    fun `should return medium confidence for CSV with only header`() {
        // Given
        val csv = "name,age,city".toByteArray()

        // When
        val confidence = detector.detect(csv)

        // Then
        assertTrue(confidence > 0.0f && confidence < 0.7f)
    }

    @Test
    fun `should return low confidence for inconsistent CSV`() {
        // Given
        val csv = """
            name,age,city
            John,30
            Jane,25,Boston,USA
        """.trimIndent().toByteArray()

        // When
        val confidence = detector.detect(csv)

        // Then
        assertTrue(confidence < 0.5f)
    }

    @Test
    fun `should return very low confidence for no commas`() {
        // Given
        val nonCsv = """
            name
            John
            Jane
        """.trimIndent().toByteArray()

        // When
        val confidence = detector.detect(nonCsv)

        // Then
        assertTrue(confidence < 0.3f)
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
}