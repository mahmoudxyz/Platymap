package xyz.mahmoudahmed.adapter.integration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.adapter.InputAdapterService
import java.io.File
import java.nio.file.Path

class IntegrationTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var adapterService: InputAdapterService

    @BeforeEach
    fun setup() {
        adapterService = InputAdapterService()
    }

    @Test
    fun `test end-to-end JSON parsing`() {
        // Create test JSON
        val json = """
            {
                "person": {
                    "name": "John Doe",
                    "age": 30,
                    "contact": {
                        "email": "john@example.com",
                        "phone": "555-1234"
                    },
                    "interests": ["programming", "music", "hiking"]
                }
            }
        """.trimIndent()

        // Parse using the service
        val result = adapterService.parseData(json)

        // Validate the parsed structure
        val person = result.asObject?.get("person")?.asObject
        assertNotNull(person)

        assertEquals("John Doe", person?.get("name")?.asString)
        assertEquals(30, person?.get("age")?.asInt)

        val contact = person?.get("contact")?.asObject
        assertEquals("john@example.com", contact?.get("email")?.asString)
        assertEquals("555-1234", contact?.get("phone")?.asString)

        val interests = person?.get("interests")?.asArray
        assertEquals(3, interests?.elements?.size)
        assertEquals("programming", interests?.get(0)?.asString)
        assertEquals("hiking", interests?.get(2)?.asString)
    }

    @Test
    fun `test end-to-end XML parsing`() {
        // Create test XML
        val xml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <library>
            <book id="1" category="fiction">
                <title>The Great Gatsby</title>
                <author>F. Scott Fitzgerald</author>
                <year>1925</year>
                <available>true</available>
            </book>
            <book id="2" category="non-fiction">
                <title>Sapiens</title>
                <author>Yuval Noah Harari</author>
                <year>2011</year>
                <available>false</available>
            </book>
        </library>
    """.trimIndent()

        // Parse using the service
        val result = adapterService.parseData(xml)

        // Validate the parsed structure
        val library = result.asObject
        assertNotNull(library)

        val books = library?.get("book")?.asArray
        assertEquals(2, books?.elements?.size)

        val book1 = books?.get(0)?.asObject
        assertNotNull(book1)

        // Get the attributes object - using properties map like in the passing test
        val book1Attrs = book1?.properties?.get("@attributes") as DataNode.ObjectNode
        assertNotNull(book1Attrs, "Book attributes should be present")

        assertEquals(1, (book1Attrs.properties["id"] as DataNode).asInt)
        assertEquals("fiction", (book1Attrs.properties["category"] as DataNode).asString)

        // Access regular element values using properties map
        assertEquals("The Great Gatsby", (book1.properties["title"] as DataNode).asString)
        assertEquals(1925, (book1.properties["year"] as DataNode).asInt)
        assertEquals(true, (book1.properties["available"] as DataNode).asBoolean)

        val book2 = books?.get(1)?.asObject
        assertEquals("Sapiens", (book2?.properties?.get("title") as DataNode).asString)
        assertEquals("Yuval Noah Harari", (book2?.properties?.get("author") as DataNode).asString)
    }

    @Test
    fun `test parsing from file`() {
        // Create test JSON file
        val jsonFile = File(tempDir.toFile(), "test.json")
        jsonFile.writeText("""{"message":"Hello from file"}""")

        // Parse using the service
        val result = adapterService.parseData(jsonFile)

        // Validate the parsed structure
        assertEquals("Hello from file", result.asObject?.get("message")?.asString)
    }

    @Test
    fun `test format auto-detection with different formats`() {
        // Test JSON detection and parsing
        val jsonData = """{"key":"json value"}"""
        val jsonResult = adapterService.parseData(jsonData)
        assertEquals("json value", jsonResult.asObject?.get("key")?.asString)

        // Test XML detection and parsing
        val xmlData = """<root><key>xml value</key></root>"""
        val xmlResult = adapterService.parseData(xmlData)
        assertEquals("xml value", xmlResult.asObject?.get("key")?.asString)
    }


}