package xyz.mahmoudahmed.dsl.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import xyz.mahmoudahmed.adapter.DataNode
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MappingContextTest {

    private lateinit var context: MappingContext
    private lateinit var sourceData: DataNode.ObjectNode

    @BeforeEach
    fun setup() {
        sourceData = DataNode.ObjectNode().apply {
            properties["name"] = DataNode.StringValue("John")
            properties["age"] = DataNode.NumberValue(30)
            properties["isActive"] = DataNode.BooleanValue(true)
            properties["address"] = DataNode.ObjectNode().apply {
                properties["city"] = DataNode.StringValue("New York")
                properties["zipCode"] = DataNode.StringValue("10001")
            }
            properties["tags"] = DataNode.ArrayNode().apply {
                elements.add(DataNode.StringValue("developer"))
                elements.add(DataNode.StringValue("kotlin"))
            }
        }

        context = MappingContext(sourceData)
    }

    @Test
    fun `setVariable and getVariable should work correctly`() {
        // When
        context.setVariable("testVar", "testValue")

        // Then
        assertEquals("testValue", context.getVariable("testVar"))
    }

    @Test
    fun `getVariable with non-existent variable should return null`() {
        // When
        val result = context.getVariable("nonExistent")

        // Then
        assertNull(result)
    }

    @Test
    fun `getVariables should return all variables`() {
        // Given
        context.setVariable("var1", "value1")
        context.setVariable("var2", "value2")

        // When
        val variables = context.getVariables()

        // Then
        assertEquals(2, variables.size)
        assertEquals("value1", variables["var1"])
        assertEquals("value2", variables["var2"])
    }

    @Test
    fun `getValueByPath with literal value should return the literal`() {
        // When
        val result = context.getValueByPath("'literal value'")

        // Then
        assertEquals("literal value", result)
    }

    @Test
    fun `getValueByPath with variable reference should return the variable value`() {
        // Given
        context.setVariable("testVar", "variable value")

        // When
        val result = context.getValueByPath("\$testVar")

        // Then
        assertEquals("variable value", result)
    }

    @Test
    fun `getValueByPath with simple path should return correct value`() {
        // When
        val result = context.getValueByPath("name")

        // Then
        assertEquals(DataNode.StringValue("John"), result)
    }

    @Test
    fun `getValueByPath with nested path should return correct value`() {
        // When
        val result = context.getValueByPath("address.city")

        // Then
        assertEquals(DataNode.StringValue("New York"), result)
    }

    @Test
    fun `getValueByPath with array index should return correct value`() {
        // When
        val result = context.getValueByPath("tags[0]")

        // Then
        assertEquals(DataNode.StringValue("developer"), result)
    }

    @Test
    fun `getValueByPath with invalid path should return null`() {
        // When
        val result = context.getValueByPath("nonexistent.path")

        // Then
        assertNull(result)
    }

    @Test
    fun `getValueByPath with invalid array index should return null`() {
        // When
        val result = context.getValueByPath("tags[99]")

        // Then
        assertNull(result)
    }

    @Test
    fun `setProperty and getProperty should work correctly`() {
        // When
        context.setProperty("testProp", "testValue")

        // Then
        assertEquals("testValue", context.getProperty("testProp"))
    }

    @Test
    fun `getProperty with non-existent property should return null`() {
        // When
        val result = context.getProperty("nonExistent")

        // Then
        assertNull(result)
    }

    @Test
    fun `getValueByPath with deeply nested path should handle null intermediate values`() {
        // When - path with null intermediate node
        val result = context.getValueByPath("nonexistent.deeper.path")

        // Then
        assertNull(result)
    }

    @Test
    fun `getValueByPath with badly formed path should throw exception`() {
        // When/Then
        assertThrows<MappingExecutionException> {
            // This would throw an exception due to illegal array access syntax
            context.getValueByPath("tags[invalidIndex]")
        }
    }
}