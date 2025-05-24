package xyz.mahmoudahmed.adapter.xml

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import xyz.mahmoudahmed.adapter.*
import xyz.mahmoudahmed.format.Format
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class XmlInputAdapter : InputAdapter {
    private val documentBuilderFactory = DocumentBuilderFactory.newInstance()

    init {
        documentBuilderFactory.isNamespaceAware = true
        documentBuilderFactory.isCoalescing = true
        documentBuilderFactory.isIgnoringComments = true
    }

    override fun canHandle(format: Format): Boolean =
        format == Format.XML

    override fun parse(data: ByteArray): DataNode =
        parse(ByteArrayInputStream(data))

    override fun parse(content: String): DataNode =
        parse(content.toByteArray())

    override fun parse(file: File): DataNode {
        val document = documentBuilderFactory.newDocumentBuilder().parse(file)
        return parseDocument(document)
    }

    override fun parse(inputStream: InputStream): DataNode {
        val document = documentBuilderFactory.newDocumentBuilder().parse(inputStream)
        return parseDocument(document)
    }

    private fun parseDocument(document: Document): DataNode {
        document.documentElement.normalize()
        return parseElement(document.documentElement)
    }

    private fun parseElement(element: Element): DataNode {
        // Special case: element with just text content and no attributes or child elements
        if (!element.hasAttributes() && element.childNodes.length == 1 &&
            element.firstChild.nodeType == Node.TEXT_NODE) {
            return handleSimpleValue(element.textContent.trim())
        }

        val result = DataNode.ObjectNode()

        // Process attributes - store in a special @attributes container
        if (element.hasAttributes()) {
            val attributesNode = DataNode.ObjectNode()
            for (i in 0 until element.attributes.length) {
                val attr = element.attributes.item(i)
                attributesNode[attr.nodeName] = handleSimpleValue(attr.nodeValue)
            }
            result["@attributes"] = attributesNode
        }

        // Process child nodes
        val childElementsByName = mutableMapOf<String, MutableList<Element>>()
        var hasTextContent = false
        var textContent = ""

        // Group child nodes by type
        val childNodes = element.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            when (node.nodeType) {
                Node.ELEMENT_NODE -> {
                    val childElement = node as Element
                    childElementsByName
                        .getOrPut(childElement.nodeName) { mutableListOf() }
                        .add(childElement)
                }
                Node.TEXT_NODE -> {
                    val text = node.textContent.trim()
                    if (text.isNotEmpty()) {
                        hasTextContent = true
                        textContent += text
                    }
                }
            }
        }

        // Add text content if present
        if (hasTextContent) {
            result["#text"] = handleSimpleValue(textContent)
        }

        // Process child elements
        childElementsByName.forEach { (name, elements) ->
            if (elements.size == 1) {
                // Single element
                result[name] = parseElement(elements[0])
            } else {
                // Create array for multiple elements with same name
                val arrayNode = DataNode.ArrayNode()
                elements.forEach { element ->
                    arrayNode.add(parseElement(element))
                }
                result[name] = arrayNode
            }
        }

        return result
    }

    private fun handleSimpleValue(value: String): DataNode {
        return when {
            value.equals("true", ignoreCase = true) -> DataNode.BooleanValue(true)
            value.equals("false", ignoreCase = true) -> DataNode.BooleanValue(false)
            value.equals("null", ignoreCase = true) -> DataNode.NullValue
            value.toIntOrNull() != null -> DataNode.NumberValue(value.toInt())
            value.toDoubleOrNull() != null -> DataNode.NumberValue(value.toDouble())
            else -> DataNode.StringValue(value)
        }
    }

    // Debug utility method to print the structure
    fun debugPrintStructure(node: DataNode, indent: String = "") {
        when (node) {
            is DataNode.ObjectNode -> {
                println("$indent{")
                node.properties.forEach { (key, value) ->
                    print("$indent  $key: ")
                    debugPrintStructure(value, "$indent  ")
                }
                println("$indent}")
            }
            is DataNode.ArrayNode -> {
                println("$indent[")
                node.elements.forEachIndexed { index, element ->
                    print("$indent  [$index] ")
                    debugPrintStructure(element, "$indent  ")
                }
                println("$indent]")
            }
            else -> println(node)
        }
    }
}