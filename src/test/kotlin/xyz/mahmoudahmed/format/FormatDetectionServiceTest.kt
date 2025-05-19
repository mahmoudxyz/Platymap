package xyz.mahmoudahmed.format

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path

class FormatDetectionServiceTest {

    private lateinit var detectionService: FormatDetectionService

    @BeforeEach
    fun setup() {
        detectionService = FormatDetectionService()
    }

    @Nested
    inner class DetectFromByteArray {

        @Test
        fun `should detect JSON format from byte array`() {
            // Given
            val jsonData = """{"name": "John", "age": 30}""".toByteArray()

            // When
            val result = detectionService.detectFormat(jsonData)

            // Then
            assertEquals(FormatType.JSON, result)
        }

        @Test
        fun `should detect XML format from byte array`() {
            // Given
            val xmlData = """<?xml version="1.0"?><root><name>John</name><age>30</age></root>""".toByteArray()

            // When
            val result = detectionService.detectFormat(xmlData)

            // Then
            assertEquals(FormatType.XML, result)
        }

        @Test
        fun `should detect CSV format from byte array`() {
            // Given
            val csvData = """name,age,city
                          |John,30,New York
                          |Jane,25,Boston""".trimMargin().toByteArray()

            // When
            val result = detectionService.detectFormat(csvData)

            // Then
            assertEquals(FormatType.CSV, result)
        }

        @Test
        fun `should detect YAML format from byte array`() {
            // Given
            val yamlData = """
                employees:
              - id: 1
                first_name: John
                last_name: Doe
                email: john.doe@example.com
                department: Engineering
                salary: 75000
            
              - id: 2
                first_name: Jane
                last_name: Smith
                email: jane.smith@example.com
                department: Marketing
                salary: 65000
    
            """.trimIndent().toByteArray()

            // When
            val result = detectionService.detectFormat(yamlData)

            // Then
            assertEquals(FormatType.YAML, result)
        }

        @Test
        fun `should return UNKNOWN for empty byte array`() {
            // Given
            val emptyData = ByteArray(0)

            // When
            val result = detectionService.detectFormat(emptyData)

            // Then
            assertEquals(FormatType.UNKNOWN, result)
        }

        @Test
        fun `should return UNKNOWN for unrecognized format`() {
            // Given
            val unknownData = "This is just a plain text with no specific format.".toByteArray()

            // When
            val result = detectionService.detectFormat(unknownData)

            println(result)
            // Then
            assertEquals(FormatType.UNKNOWN, result)
        }

    }

    @Nested
    inner class DetectFromInputStream {

        @Test
        fun `should detect format from input stream`() {
            // Given
            val jsonData = """{"name": "John", "age": 30}"""
            val inputStream = ByteArrayInputStream(jsonData.toByteArray())

            // When
            val result = detectionService.detectFormat(inputStream)

            // Then
            assertEquals(FormatType.JSON, result)
        }

        @Test
        fun `should handle empty input stream`() {
            // Given
            val emptyStream = ByteArrayInputStream(ByteArray(0))

            // When
            val result = detectionService.detectFormat(emptyStream)

            // Then
            assertEquals(FormatType.UNKNOWN, result)
        }
    }

    @Nested
    inner class DetectFromFile {

        @TempDir
        lateinit var tempDir: Path

        @Test
        fun `should detect format from file extension`() {
            // Given
            val jsonFile = File(tempDir.toFile(), "test.json")
            jsonFile.createNewFile()

            // When
            val result = detectionService.detectFormat(jsonFile)

            // Then
            assertEquals(FormatType.JSON, result)
        }

        @Test
        fun `should fallback to content detection when extension is unknown`() {
            // Given
            val jsonFile = File(tempDir.toFile(), "test.dat")
            FileOutputStream(jsonFile).use {
                it.write("""{"name": "John", "age": 30}""".toByteArray())
            }

            // When
            val result = detectionService.detectFormat(jsonFile)

            // Then
            assertEquals(FormatType.JSON, result)
        }

        @Test
        fun `should detect various file formats by extension`() {
            // Test all supported extensions
            val extensions = mapOf(
                "json" to FormatType.JSON,
                "xml" to FormatType.XML,
                "csv" to FormatType.CSV,
                "yml" to FormatType.YAML,
                "yaml" to FormatType.YAML,
                "properties" to FormatType.PROPERTIES,
                "avro" to FormatType.AVRO,
                "proto" to FormatType.PROTOBUF,
                "parquet" to FormatType.PARQUET,
                "xls" to FormatType.EXCEL,
                "xlsx" to FormatType.EXCEL
            )

            extensions.forEach { (ext, type) ->
                val file = File(tempDir.toFile(), "test.$ext")
                file.createNewFile()

                assertEquals(type, detectionService.detectFormat(file), "Failed for extension $ext")
            }
        }
    }


}
