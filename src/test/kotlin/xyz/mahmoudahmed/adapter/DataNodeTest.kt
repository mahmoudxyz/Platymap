package xyz.mahmoudahmed.adapter


import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DataNodeTest {

    @Test
    fun `test ObjectNode operations`() {
        val objectNode = DataNode.ObjectNode()

        // Test setting and getting values
        objectNode["name"] = DataNode.StringValue("John")
        objectNode["age"] = DataNode.NumberValue(30)

        assertEquals("John", objectNode["name"]?.asString)
        assertEquals(30, objectNode["age"]?.asInt)
        assertNull(objectNode["address"])

        // Test overwriting existing value
        objectNode["name"] = DataNode.StringValue("Jane")
        assertEquals("Jane", objectNode["name"]?.asString)
    }

    @Test
    fun `test ArrayNode operations`() {
        val arrayNode = DataNode.ArrayNode()

        // Test adding and accessing elements
        arrayNode.add(DataNode.StringValue("First"))
        arrayNode.add(DataNode.NumberValue(42))

        assertEquals("First", arrayNode[0]?.asString)
        assertEquals(42, arrayNode[1]?.asInt)
        assertNull(arrayNode[2])

        // Test adding more complex structures
        val personObject = DataNode.ObjectNode().apply {
            this["name"] = DataNode.StringValue("Alice")
        }
        arrayNode.add(personObject)

        assertEquals("Alice", arrayNode[2]?.asObject?.get("name")?.asString)
    }

    @Test
    fun `test value accessors`() {
        val stringNode = DataNode.StringValue("Test")
        val intNode = DataNode.NumberValue(42)
        val doubleNode = DataNode.NumberValue(3.14)
        val boolNode = DataNode.BooleanValue(true)
        val nullNode = DataNode.NullValue

        // Test type-specific accessors
        assertEquals("Test", stringNode.asString)
        assertEquals(42, intNode.asInt)
        assertEquals(3.14, doubleNode.asDouble)
        assertEquals(true, boolNode.asBoolean)
        assertTrue(nullNode.isNull)

        // Test wrong type accessors
        assertNull(stringNode.asInt)
        assertNull(intNode.asString)
        assertNull(boolNode.asObject)
        assertNull(nullNode.asArray)
    }

    @Test
    fun `test complex nested structure`() {
        // Create a complex nested structure
        val root = DataNode.ObjectNode().apply {
            this["title"] = DataNode.StringValue("My Document")
            this["count"] = DataNode.NumberValue(3)
            this["items"] = DataNode.ArrayNode().apply {
                add(DataNode.ObjectNode().apply {
                    this["id"] = DataNode.NumberValue(1)
                    this["name"] = DataNode.StringValue("Item 1")
                })
                add(DataNode.ObjectNode().apply {
                    this["id"] = DataNode.NumberValue(2)
                    this["name"] = DataNode.StringValue("Item 2")
                })
            }
        }

        // Verify the structure
        assertEquals("My Document", root["title"]?.asString)
        assertEquals(3, root["count"]?.asInt)
        assertEquals(2, root["items"]?.asArray?.elements?.size)
        assertEquals(1, root["items"]?.asArray?.get(0)?.asObject?.get("id")?.asInt)
        assertEquals("Item 2", root["items"]?.asArray?.get(1)?.asObject?.get("name")?.asString)
    }
}