package xyz.mahmoudahmed.dsl.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import xyz.mahmoudahmed.adapter.DataNode
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SimpleMappingTest {

    private lateinit var sourceData: DataNode.ObjectNode
    private lateinit var context: MappingContext
    private lateinit var target: DataNode.ObjectNode

    @BeforeEach
    fun setup() {
        sourceData = DataNode.ObjectNode().apply {
            properties["name"] = DataNode.StringValue("John")
            properties["age"] = DataNode.NumberValue(30)
            properties["isActive"] = DataNode.BooleanValue(true)
            properties["address"] = DataNode.ObjectNode().apply {
                properties["city"] = DataNode.StringValue("New York")
            }
        }

        context = MappingContext(sourceData)
        target = DataNode.ObjectNode()
    }

    @Test
    fun `apply should map simple value without transformation`() {
        // Given
        val mapping = SimpleMapping("name", "userName", null, null)

        // When
        mapping.apply(context, target)

        // Then
        assertEquals("John", target.get("userName")?.asString)
    }

    @Test
    fun `apply should map nested value to target`() {
        // Given
        val mapping = SimpleMapping("address.city", "userCity", null, null)

        // When
        mapping.apply(context, target)

        // Then
        assertEquals("New York", target.get("userCity")?.asString)
    }

    @Test
    fun `apply should map value to nested target path`() {
        // Given
        val mapping = SimpleMapping("name", "user.displayName", null, null)

        // When
        mapping.apply(context, target)

        // Then
        val userNode = target.get("user") as DataNode.ObjectNode
        assertEquals("John", userNode.get("displayName")?.asString)
    }

    @Test
    fun `apply should transform value when transformation is provided`() {
        // Given
        val transformation: (Any) -> Any = {
            if (it is DataNode.StringValue) DataNode.StringValue(it.value.uppercase()) else it
        }
        val mapping = SimpleMapping("name", "userName", transformation, null)

        // When
        mapping.apply(context, target)

        // Then
        assertEquals("JOHN", target.get("userName")?.asString)
    }

    @Test
    fun `apply should not map when condition returns false`() {
        // Given
        val condition: (Any) -> Boolean = { false } // Always false
        val mapping = SimpleMapping("name", "userName", null, condition)

        // When
        mapping.apply(context, target)

        // Then
        assertNull(target.get("userName"))
    }

    @Test
    fun `apply should map when condition returns true`() {
        // Given
        val condition: (Any) -> Boolean = { true } // Always true
        val mapping = SimpleMapping("name", "userName", null, condition)

        // When
        mapping.apply(context, target)

        // Then
        assertEquals("John", target.get("userName")?.asString)
    }

    @Test
    fun `apply should handle transformation errors`() {
        // Given
        val transformation: (Any) -> Any = { throw RuntimeException("Test error") }
        val mapping = SimpleMapping("name", "userName", transformation, null)

        // When/Then
        val exception = assertThrows<MappingExecutionException> {
            mapping.apply(context, target)
        }

        assertTrue(exception.message?.contains("Error applying transformation") ?: false)
    }

    @Test
    fun `apply should handle non-DataNode target errors`() {
        // Given
        val mapping = SimpleMapping("name", "userName", null, null)
        val invalidTarget = "not a DataNode"

        // When/Then
        val exception = assertThrows<MappingExecutionException> {
            mapping.apply(context, invalidTarget)
        }

        assertTrue(exception.message?.contains("Target must be a DataNode.ObjectNode") ?: false)
    }

    @Test
    fun `apply should handle null source value`() {
        // Given
        val mapping = SimpleMapping("nonExistentPath", "userName", null, null)

        // When
        mapping.apply(context, target)

        // Then
        assertNull(target.get("userName"))
    }

    @Test
    fun `setValueInDataNode should create nested structure`() {
        // When
        SimpleMapping.setValueInDataNode(target, "user.profile.firstName", "John")

        // Then
        val userNode = target.get("user") as DataNode.ObjectNode
        val profileNode = userNode.get("profile") as DataNode.ObjectNode
        assertEquals("John", profileNode.get("firstName")?.asString)
    }

    @Test
    fun `convertToDataNode should handle various types`() {
        // Test null value
        assertEquals(DataNode.NullValue, SimpleMapping.convertToDataNode(null))

        // Test string value
        val stringNode = SimpleMapping.convertToDataNode("test")
        assertTrue(stringNode is DataNode.StringValue)
        assertEquals("test", (stringNode as DataNode.StringValue).value)

        // Test number value
        val numberNode = SimpleMapping.convertToDataNode(42)
        assertTrue(numberNode is DataNode.NumberValue)
        assertEquals(42, (numberNode as DataNode.NumberValue).value)

        // Test boolean value
        val booleanNode = SimpleMapping.convertToDataNode(true)
        assertTrue(booleanNode is DataNode.BooleanValue)
        assertEquals(true, (booleanNode as DataNode.BooleanValue).value)

        // Test map value
        val mapNode = SimpleMapping.convertToDataNode(mapOf("key" to "value"))
        assertTrue(mapNode is DataNode.ObjectNode)
        assertEquals("value", (mapNode as DataNode.ObjectNode).get("key")?.asString)

        // Test list value
        val listNode = SimpleMapping.convertToDataNode(listOf("item1", "item2"))
        assertTrue(listNode is DataNode.ArrayNode)
        assertEquals(2, (listNode as DataNode.ArrayNode).elements.size)
        assertEquals("item1", listNode.elements[0].asString)
    }
}