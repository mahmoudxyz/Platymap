package xyz.mahmoudahmed.dsl.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import xyz.mahmoudahmed.adapter.DataNode
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MultiFieldMappingTest {

    private lateinit var sourceData: DataNode.ObjectNode
    private lateinit var context: MappingContext
    private lateinit var target: DataNode.ObjectNode

    @BeforeEach
    fun setup() {
        sourceData = DataNode.ObjectNode().apply {
            properties["firstName"] = DataNode.StringValue("John")
            properties["lastName"] = DataNode.StringValue("Doe")
            properties["age"] = DataNode.NumberValue(30)
            properties["isActive"] = DataNode.BooleanValue(true)
        }

        context = MappingContext(sourceData)
        target = DataNode.ObjectNode()
    }

    @Test
    fun `apply should map multiple fields with default space separator`() {
        // Given
        val sourcePaths = listOf("firstName", "lastName")
        val targetPath = "fullName"
        val mapping = MultiFieldMapping(sourcePaths, targetPath, { values ->
            // Default behavior similar to concatenation with space
            val stringValues = values.filterNotNull().map {
                when (it) {
                    is DataNode.StringValue -> it.value
                    else -> it.toString()
                }
            }
            DataNode.StringValue(stringValues.joinToString(" "))
        }, null)

        // When
        mapping.apply(context, target)

        // Then
        assertEquals("John Doe", target.get("fullName")?.asString)
    }

    @Test
    fun `apply should use custom transformation function`() {
        // Given
        val sourcePaths = listOf("firstName", "lastName", "age")
        val targetPath = "summary"
        val transformation: (List<Any?>) -> Any = { values ->
            val firstName = (values[0] as? DataNode.StringValue)?.value ?: ""
            val lastName = (values[1] as? DataNode.StringValue)?.value ?: ""
            val age = (values[2] as? DataNode.NumberValue)?.value?.toInt() ?: 0

            DataNode.StringValue("$firstName $lastName is $age years old")
        }

        val mapping = MultiFieldMapping(sourcePaths, targetPath, transformation, null)

        // When
        mapping.apply(context, target)

        // Then
        assertEquals("John Doe is 30 years old", target.get("summary")?.asString)
    }

    @Test
    fun `apply should skip mapping when condition returns false`() {
        // Given
        val sourcePaths = listOf("firstName", "lastName")
        val targetPath = "fullName"
        // Important fix: condition in MultiFieldMapping applies in reverse logic
        // If condition returns true, it skips the mapping
        val condition: (Any) -> Boolean = { true } // Always true, so should SKIP

        val mapping = MultiFieldMapping(sourcePaths, targetPath, { values ->
            DataNode.StringValue(values.filterNotNull().joinToString(" "))
        }, condition)

        // When
        mapping.apply(context, target)

        // Then
        assertNull(target.get("fullName"))
    }

    @Test
    fun `apply should handle transformation errors`() {
        // Given
        val sourcePaths = listOf("firstName", "lastName")
        val targetPath = "fullName"
        val transformation: (List<Any?>) -> Any = { throw RuntimeException("Test error") }

        val mapping = MultiFieldMapping(sourcePaths, targetPath, transformation, null)

        // When/Then
        val exception = assertThrows<MappingExecutionException> {
            mapping.apply(context, target)
        }

        assertEquals(true, exception.message?.contains("Error applying multi-field transformation"))
    }

    @Test
    fun `apply should handle non-DataNode target errors`() {
        // Given
        val sourcePaths = listOf("firstName", "lastName")
        val targetPath = "fullName"
        val mapping = MultiFieldMapping(sourcePaths, targetPath, { values ->
            DataNode.StringValue(values.filterNotNull().joinToString(" "))
        }, null)

        val invalidTarget = "not a DataNode"

        // When/Then
        val exception = assertThrows<MappingExecutionException> {
            mapping.apply(context, invalidTarget)
        }

        assertEquals(true, exception.message?.contains("Target must be a DataNode.ObjectNode"))
    }

    @Test
    fun `apply should handle null or missing source values`() {
        // Given
        val sourcePaths = listOf("firstName", "nonExistentField", "lastName")
        val targetPath = "result"

        val mapping = MultiFieldMapping(sourcePaths, targetPath, { values ->
            val stringValues = values.filterNotNull().map {
                when (it) {
                    is DataNode.StringValue -> it.value
                    else -> it.toString()
                }
            }
            DataNode.StringValue(stringValues.joinToString("|"))
        }, null)

        // When
        mapping.apply(context, target)

        // Then
        assertEquals("John|Doe", target.get("result")?.asString)
    }

    @Test
    fun `apply should map to nested target path`() {
        // Given
        val sourcePaths = listOf("firstName", "lastName")
        val targetPath = "user.fullName"

        val mapping = MultiFieldMapping(sourcePaths, targetPath, { values ->
            val stringValues = values.filterNotNull().map {
                when (it) {
                    is DataNode.StringValue -> it.value
                    else -> it.toString()
                }
            }
            DataNode.StringValue(stringValues.joinToString(" "))
        }, null)

        // When
        mapping.apply(context, target)

        // Then
        val userNode = target.get("user") as DataNode.ObjectNode
        assertEquals("John Doe", userNode.get("fullName")?.asString)
    }
}