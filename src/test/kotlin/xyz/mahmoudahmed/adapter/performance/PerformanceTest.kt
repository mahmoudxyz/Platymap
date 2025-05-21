package xyz.mahmoudahmed.adapter.performance

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag
import xyz.mahmoudahmed.adapter.InputAdapterService
import java.io.ByteArrayInputStream
import kotlin.system.measureTimeMillis
import kotlin.test.Ignore

@Tag("performance")
class PerformanceTest {

    private val adapterService = InputAdapterService()

    @Test
    @Ignore
    fun `test JSON parsing performance with large document`() {
        // Generate a large JSON document
        val largeJson = generateLargeJson(10000)

        // Measure parsing time
        val parsingTime = measureTimeMillis {
            val result = adapterService.parseData(largeJson)
            // Access some values to ensure the parsing is complete
            val firstItem = result.asObject?.get("items")?.asArray?.get(0)?.asObject
            requireNotNull(firstItem?.get("id")?.asInt)
        }
        println("JSON parsing time for 10,000 items: $parsingTime ms")

        // The performance test doesn't have explicit assertions but should complete
        // within a reasonable time frame (could add threshold assertions for CI environments)
    }

    @Test
    fun `test XML parsing performance with large document`() {
        // Generate a large XML document
        val largeXml = generateLargeXml(5000)

        // Measure parsing time
        val parsingTime = measureTimeMillis {
            val result = adapterService.parseData(largeXml)
            // Access some values to ensure the parsing is complete
            val firstItem = result.asObject?.get("item")?.asArray?.get(0)?.asObject
            requireNotNull(firstItem?.get("name")?.asString)
        }

        println("XML parsing time for 5,000 items: $parsingTime ms")
    }

    @Test
    @Ignore
    fun `test streaming performance with large input stream`() {
        // Generate a large JSON document as a stream
        val largeJson = generateLargeJson(20000)
        val inputStream = ByteArrayInputStream(largeJson.toByteArray())

        // Measure parsing time
        val parsingTime = measureTimeMillis {
            val result = adapterService.parseData(inputStream)
            // Access some random items to verify complete parsing
            val items = result.asObject?.get("items")?.asArray?.elements ?: emptyList()
            val randomIndex = (items.size / 2)
            val randomItem = items[randomIndex].asObject
            requireNotNull(randomItem?.get("id")?.asInt)
        }

        println("Streaming JSON parse time for 20,000 items: $parsingTime ms")
    }

    @Test
    @Ignore
    fun `test memory usage during large document parsing`() {
        // This test is more observational - it logs memory usage before and after
        // a large parse operation to help identify memory leaks or inefficiencies

        System.gc() // Request garbage collection before test
        val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

        // Parse a very large document
        val largeJson = generateLargeJson(50000)
        val result = adapterService.parseData(largeJson)

        // Force processing by accessing some nested elements
        result.asObject?.get("items")?.asArray?.elements?.take(100)?.forEach {
            requireNotNull(it.asObject?.get("id"))
        }

        System.gc() // Request garbage collection after test
        val finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

        println("Memory before: ${initialMemory / (1024 * 1024)} MB")
        println("Memory after: ${finalMemory / (1024 * 1024)} MB")
        println("Difference: ${(finalMemory - initialMemory) / (1024 * 1024)} MB")
    }

    // Helper function to generate large JSON test data
    private fun generateLargeJson(itemCount: Int): String {
        val sb = StringBuilder()
        sb.append("""{"items":[""")

        for (i in 0 until itemCount) {
            if (i > 0) sb.append(",")
            sb.append("""
                {
                    "id": $i,
                    "name": "Item $i",
                    "description": "This is a test item with index $i",
                    "tags": ["tag1", "tag2", "tag3"],
                    "properties": {
                        "color": "red",
                        "size": ${i % 10},
                        "active": ${i % 2 == 0}
                    }
                }
            """.trimIndent())
        }

        sb.append("]}")
        return sb.toString()
    }

    // Helper function to generate large XML test data
    private fun generateLargeXml(itemCount: Int): String {
        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8"?><root>""")

        for (i in 0 until itemCount) {
            sb.append("""
                <item id="$i">
                    <name>Item $i</name>
                    <description>This is a test item with index $i</description>
                    <tags>
                        <tag>tag1</tag>
                        <tag>tag2</tag>
                        <tag>tag3</tag>
                    </tags>
                    <properties>
                        <color>red</color>
                        <size>${i % 10}</size>
                        <active>${i % 2 == 0}</active>
                    </properties>
                </item>
            """.trimIndent())
        }

        sb.append("</root>")
        return sb.toString()
    }
}