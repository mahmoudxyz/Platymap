package xyz.mahmoudahmed.adapter.json

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.format.FormatType
import java.nio.file.Files

class JsonInputAdapterTest {
    private val adapter = JsonInputAdapter()

    @Test
    fun `test can handle JSON format`() {
        assertTrue(adapter.canHandle(FormatType.JSON))
        assertFalse(adapter.canHandle(FormatType.XML))
        assertFalse(adapter.canHandle(FormatType.CSV))
    }

    @Test
    fun `test parse simple JSON string`() {
        val json = """{"name":"John","age":30,"isActive":true,"score":null}"""
        val result = adapter.parse(json)

        assertTrue(result is DataNode.ObjectNode)
        val obj = result as DataNode.ObjectNode

        assertEquals("John", obj["name"]?.asString)
        assertEquals(30, obj["age"]?.asInt)
        assertEquals(true, obj["isActive"]?.asBoolean)
        assertTrue(obj["score"]?.isNull ?: false)
    }

    @Test
    fun `test parse JSON with arrays`() {
        val json = """{"items":[1,2,3],"names":["Alice","Bob"]}"""
        val result = adapter.parse(json)

        val obj = result as DataNode.ObjectNode
        val items = obj["items"]?.asArray
        val names = obj["names"]?.asArray

        assertNotNull(items)
        assertNotNull(names)

        assertEquals(3, items?.elements?.size)
        assertEquals(2, names?.elements?.size)

        assertEquals(1, items?.get(0)?.asInt)
        assertEquals(3, items?.get(2)?.asInt)
        assertEquals("Alice", names?.get(0)?.asString)
        assertEquals("Bob", names?.get(1)?.asString)
    }

    @Test
    fun `test parse nested JSON objects`() {
        val json = """
            {
                "person": {
                    "name": "John",
                    "address": {
                        "city": "New York",
                        "zipcode": "10001"
                    }
                }
            }
        """.trimIndent()

        val result = adapter.parse(json)
        val obj = result as DataNode.ObjectNode

        val person = obj["person"]?.asObject
        assertNotNull(person)

        assertEquals("John", person?.get("name")?.asString)

        val address = person?.get("address")?.asObject
        assertNotNull(address)

        assertEquals("New York", address?.get("city")?.asString)
        assertEquals("10001", address?.get("zipcode")?.asString)
    }

    @Test
    fun `test parse complex JSON with mixed types`() {
        val json = """
            {
                "id": 1,
                "name": "Product",
                "price": 29.99,
                "tags": ["new", "sale"],
                "details": {
                    "weight": 0.5,
                    "dimensions": {
                        "width": 10,
                        "height": 20,
                        "unit": "cm"
                    }
                },
                "available": true,
                "related_ids": [101, 102, 103],
                "description": null
            }
        """.trimIndent()

        val result = adapter.parse(json)
        val obj = result as DataNode.ObjectNode

        assertEquals(1, obj["id"]?.asInt)
        assertEquals("Product", obj["name"]?.asString)
        assertEquals(29.99, obj["price"]?.asDouble)

        val tags = obj["tags"]?.asArray
        assertEquals(2, tags?.elements?.size)
        assertEquals("new", tags?.get(0)?.asString)

        val details = obj["details"]?.asObject
        assertEquals(0.5, details?.get("weight")?.asDouble)

        val dimensions = details?.get("dimensions")?.asObject
        assertEquals(10, dimensions?.get("width")?.asInt)
        assertEquals("cm", dimensions?.get("unit")?.asString)

        assertEquals(true, obj["available"]?.asBoolean)

        val relatedIds = obj["related_ids"]?.asArray
        assertEquals(3, relatedIds?.elements?.size)
        assertEquals(102, relatedIds?.get(1)?.asInt)

        assertTrue(obj["description"]?.isNull ?: false)
    }

    @Test
    fun `test parse from byte array`() {
        val jsonBytes = """{"key":"value"}""".toByteArray()
        val result = adapter.parse(jsonBytes)

        assertEquals("value", result.asObject?.get("key")?.asString)
    }

    @Test
    fun `test parse from file`() {
        // Create a temporary file for testing
        val tempFile = Files.createTempFile("test", ".json").toFile()
        tempFile.writeText("""{"key":"fileValue"}""")
        tempFile.deleteOnExit()

        val result = adapter.parse(tempFile)
        assertEquals("fileValue", result.asObject?.get("key")?.asString)
    }

    @Test
    fun `test parse from input stream`() {
        val jsonContent = """{"key":"streamValue"}"""
        val inputStream = jsonContent.byteInputStream()

        val result = adapter.parse(inputStream)
        assertEquals("streamValue", result.asObject?.get("key")?.asString)
    }
}