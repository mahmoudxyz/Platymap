package xyz.mahmoudahmed.adapter.xml

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.format.Format

class XmlInputAdapterTest {
    private val adapter = XmlInputAdapter()

    @Test
    fun `test can handle XML format`() {
        assertTrue(adapter.canHandle(Format.XML))
        assertFalse(adapter.canHandle(Format.JSON))
    }

    @Test
    fun `debug test to print XML structure`() {
        val xml = """
            <product id="123" category="electronics">
                <name>Smartphone</name>
                <price currency="USD">599.99</price>
            </product>
        """.trimIndent()

        val result = adapter.parse(xml)
        adapter.debugPrintStructure(result)
        // Let's inspect what's actually in the structure instead of guessing
    }

    @Test
    fun `test parse simple XML string`() {
        val xml = """
            <person>
                <name>John</name>
                <age>30</age>
                <active>true</active>
            </person>
        """.trimIndent()

        val result = adapter.parse(xml) as DataNode.ObjectNode

        assertEquals("John", (result.properties["name"] as DataNode).asString)
        assertEquals(30, (result.properties["age"] as DataNode).asInt)
        assertEquals(true, (result.properties["active"] as DataNode).asBoolean)
    }

    @Test
    fun `test parse XML with attributes`() {
        val xml = """
            <product id="123" category="electronics">
                <name>Smartphone</name>
                <price currency="USD">599.99</price>
            </product>
        """.trimIndent()

        val result = adapter.parse(xml) as DataNode.ObjectNode

        // Access attributes from the @attributes container
        val attributes = result.properties["@attributes"] as DataNode.ObjectNode
        assertEquals(123, (attributes.properties["id"] as DataNode).asInt)
        assertEquals("electronics", (attributes.properties["category"] as DataNode).asString)

        // Test child elements
        assertEquals("Smartphone", (result.properties["name"] as DataNode).asString)

        val price = result.properties["price"] as DataNode.ObjectNode
        val priceAttributes = price.properties["@attributes"] as DataNode.ObjectNode
        assertEquals("USD", (priceAttributes.properties["currency"] as DataNode).asString)
        assertEquals(599.99, (price.properties["#text"] as DataNode).asDouble)
    }

    @Test
    fun `test parse XML with repeated elements as arrays`() {
        val xml = """
            <order>
                <id>12345</id>
                <item>
                    <sku>ABC123</sku>
                    <quantity>2</quantity>
                </item>
                <item>
                    <sku>XYZ789</sku>
                    <quantity>1</quantity>
                </item>
            </order>
        """.trimIndent()

        val result = adapter.parse(xml) as DataNode.ObjectNode

        assertEquals(12345, (result.properties["id"] as DataNode).asInt)

        val items = result.properties["item"] as DataNode.ArrayNode
        assertEquals(2, items.elements.size)

        val item1 = items.elements[0] as DataNode.ObjectNode
        assertEquals("ABC123", (item1.properties["sku"] as DataNode).asString)
        assertEquals(2, (item1.properties["quantity"] as DataNode).asInt)

        val item2 = items.elements[1] as DataNode.ObjectNode
        assertEquals("XYZ789", (item2.properties["sku"] as DataNode).asString)
    }

    @Test
    fun `test parse nested XML structures`() {
        val xml = """
            <company>
                <name>ACME Corp</name>
                <headquarters>
                    <address>
                        <street>123 Main St</street>
                        <city>New York</city>
                        <zipcode>10001</zipcode>
                    </address>
                    <phone>555-1234</phone>
                </headquarters>
            </company>
        """.trimIndent()

        val result = adapter.parse(xml) as DataNode.ObjectNode

        // Access deeply nested property through chain of gets
        val headquarters = result.properties["headquarters"] as DataNode.ObjectNode
        val address = headquarters.properties["address"] as DataNode.ObjectNode
        val zipcode = address.properties["zipcode"] as DataNode

        assertEquals(10001, zipcode.asInt)
    }

    @Test
    fun `test parse XML with CDATA sections`() {
        val xml = """
            <document>
                <title>User Guide</title>
                <content><![CDATA[This is <b>raw text</b> that shouldn't be parsed as XML.]]></content>
            </document>
        """.trimIndent()

        val result = adapter.parse(xml) as DataNode.ObjectNode

        assertEquals("User Guide", (result.properties["title"] as DataNode).asString)
        assertEquals("This is <b>raw text</b> that shouldn't be parsed as XML.",
            (result.properties["content"] as DataNode).asString)
    }

    @Test
    fun `test parse from byte array`() {
        val xmlBytes = "<root><key>value</key></root>".toByteArray()
        val result = adapter.parse(xmlBytes) as DataNode.ObjectNode

        assertEquals("value", (result.properties["key"] as DataNode).asString)
    }

    @Test
    fun `test parse empty elements and elements with only attributes`() {
        val xml = """
            <data>
                <emptyElement></emptyElement>
                <selfClosing/>
                <withAttribute attr="value"/>
            </data>
        """.trimIndent()

        val result = adapter.parse(xml) as DataNode.ObjectNode

        // Empty elements should be empty object nodes
        assertTrue(result.properties["emptyElement"] is DataNode.ObjectNode)
        assertTrue(result.properties["selfClosing"] is DataNode.ObjectNode)

        val withAttribute = result.properties["withAttribute"] as DataNode.ObjectNode
        val attrObj = withAttribute.properties["@attributes"] as DataNode.ObjectNode
        assertEquals("value", (attrObj.properties["attr"] as DataNode).asString)
    }
}