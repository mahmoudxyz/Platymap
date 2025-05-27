package xyz.mahmoudahmed.dsl.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.adapter.InputAdapterService
import xyz.mahmoudahmed.format.Format
import java.io.File
import java.io.StringWriter
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MappingTest {

    private lateinit var testRule: TestMappingRule
    private lateinit var mapping: Mapping

    // Test implementation of MappingRule
    class TestMappingRule : MappingRule {
        var applied = false
        var appliedContext: MappingContext? = null
        var appliedTarget: Any? = null

        override fun apply(context: MappingContext, target: Any) {
            applied = true
            appliedContext = context
            appliedTarget = target

            // Add some data to the target for verification
            if (target is DataNode.ObjectNode) {
                target.properties["testKey"] = DataNode.StringValue("testValue")
            }
        }
    }

    @BeforeEach
    fun setup() {
        testRule = TestMappingRule()
        mapping = Mapping(
            sourceName = "testSource",
            sourceFormat = Format.JSON,
            targetName = "testTarget",
            targetFormat = Format.JSON,
            rules = listOf(testRule),
            properties = mapOf("testProp" to "testValue")
        )
    }

    @Test
    fun `execute with string source data should apply rules`() {
        // Given
        val sourceData = """{"name": "John"}"""

        // When
        val result = mapping.execute(sourceData)

        // Then
        assertTrue(testRule.applied)
        assertTrue(result is DataNode.ObjectNode)
        assertEquals("testValue", (result as DataNode.ObjectNode).properties["testKey"]?.asString)
    }

    @Test
    fun `execute with DataNode source data should apply rules`() {
        // Given
        val sourceData = DataNode.ObjectNode().apply {
            properties["name"] = DataNode.StringValue("John")
        }

        // When
        val result = mapping.execute(sourceData)

        // Then
        assertTrue(testRule.applied)
        assertTrue(result is DataNode.ObjectNode)
        assertEquals("testValue", (result as DataNode.ObjectNode).properties["testKey"]?.asString)
    }

    @Test
    fun `execute with unsupported source data should throw exception`() {
        // Given
        val sourceData = 123 // Integer is not a supported source data type

        // When/Then
        assertThrows<MappingExecutionException> {
            mapping.execute(sourceData)
        }
    }

    @Test
    fun `execute with rule that throws exception should wrap in MappingExecutionException`() {
        // Given
        val errorRule = object : MappingRule {
            override fun apply(context: MappingContext, target: Any) {
                throw RuntimeException("Test error")
            }
        }

        val errorMapping = Mapping(
            sourceName = "testSource",
            sourceFormat = Format.JSON,
            targetName = "testTarget",
            targetFormat = Format.JSON,
            rules = listOf(errorRule)
        )

        val sourceData = """{"name": "John"}"""

        // When/Then
        val exception = assertThrows<MappingExecutionException> {
            errorMapping.execute(sourceData)
        }

        assertTrue(exception.message?.contains("Error during mapping execution") ?: false)
    }

    @Test
    fun `executeToFormat should return serialized result`() {
        // Given
        val sourceData = """{"name": "John"}"""

        // When
        val result = mapping.executeToFormat(sourceData, Format.JSON)

        // Then
        assertTrue(result.contains("testKey"))
        assertTrue(result.contains("testValue"))
    }

    @Test
    fun `executeToFormat with writer should write to writer`() {
        // Given
        val sourceData = """{"name": "John"}"""
        val writer = StringWriter()

        // When
        mapping.executeToFormat(sourceData, Format.JSON, writer)

        // Then
        val result = writer.toString()
        assertTrue(result.contains("testKey"))
        assertTrue(result.contains("testValue"))
    }

    @Test
    fun `executeToFormat with output stream should write to stream`() {
        // Given
        val sourceData = """{"name": "John"}"""
        val outputStream = ByteArrayOutputStream()

        // When
        mapping.executeToFormat(sourceData, Format.JSON, outputStream)

        // Then
        val result = outputStream.toString("UTF-8")
        assertTrue(result.contains("testKey"))
        assertTrue(result.contains("testValue"))
    }

    @Test
    fun `executeToJson should return JSON result`() {
        // Given
        val sourceData = """{"name": "John"}"""

        // When
        val result = mapping.executeToJson(sourceData)

        // Then
        assertTrue(result.contains("testKey"))
        assertTrue(result.contains("testValue"))
    }

    @Test
    fun `executeToXml should return XML result`() {
        // Given
        val sourceData = """{"name": "John"}"""

        // When
        val result = mapping.executeToXml(sourceData)

        // Then
        assertTrue(result.contains("testKey"))
        assertTrue(result.contains("testValue"))
    }
}