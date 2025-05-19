package xyz.mahmoudahmed.format
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class XmlFormatDetectorTest {

    private val detector = XmlFormatDetector()

    @Test
    fun `should return high confidence for valid XML with declaration`() {
        // Given
        val xml = """<?xml version="1.0"?><root><name>John</name></root>""".toByteArray()

        // When
        val confidence = detector.detect(xml)

        // Then
        assertTrue(confidence > 0.7f)
    }

    @Test
    fun `should return mid confidence for valid XML without declaration`() {
        // Given
        val xml = """<root><name>John</name></root>""".toByteArray()

        // When
        val confidence = detector.detect(xml)

        println(confidence)
        // Then
        assertTrue(confidence >= 0.5f)
    }

    @Test
    fun `should return medium confidence for partial XML`() {
        // Given
        val xml = """<root><name>John</name>""".toByteArray() // Missing closing tag

        // When
        val confidence = detector.detect(xml)

        // Then
        assertTrue(confidence > 0.0f && confidence < 0.9f)
    }

    @Test
    fun `should return zero confidence for non-XML`() {
        // Given
        val nonXml = "This is just plain text".toByteArray()

        // When
        val confidence = detector.detect(nonXml)

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
    fun `should handle whitespace in XML`() {
        // Given
        val xml = """
            <?xml version="1.0"?>
            <root>
                <name>John</name>
                <age>30</age>
            </root>
        """.trimIndent().toByteArray()

        // When
        val confidence = detector.detect(xml)

        // Then
        assertTrue(confidence > 0.8f)
    }
}
