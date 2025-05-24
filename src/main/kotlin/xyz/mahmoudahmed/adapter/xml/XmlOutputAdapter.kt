package xyz.mahmoudahmed.adapter.xml

import org.w3c.dom.Document
import org.w3c.dom.Element
import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.adapter.OutputAdapter
import xyz.mahmoudahmed.format.Format
import java.io.OutputStream
import java.io.StringWriter
import java.io.Writer
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class XmlOutputAdapter : OutputAdapter {
    override fun canHandle(format: Format): Boolean =
        format == Format.XML

    override fun serialize(node: DataNode): String {
        val stringWriter = StringWriter()
        serialize(node, stringWriter)
        return stringWriter.toString()
    }

    override fun serialize(node: DataNode, writer: Writer) {
        val document = createXmlDocument(node)
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(DOMSource(document), StreamResult(writer))
    }

    override fun serialize(node: DataNode, outputStream: OutputStream) {
        val document = createXmlDocument(node)
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(DOMSource(document), StreamResult(outputStream))
    }

    private fun createXmlDocument(node: DataNode): Document {
        val docFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = docFactory.newDocumentBuilder()
        val document = docBuilder.newDocument()

        when (node) {
            is DataNode.ObjectNode -> {
                // Find a suitable root element name
                val rootName = findRootElementName(node)
                val rootElement = document.createElement(rootName)
                document.appendChild(rootElement)

                // Process all properties
                node.properties.forEach { (key, value) ->
                    appendNodeToElement(document, rootElement, key, value)
                }
            }
            is DataNode.ArrayNode -> {
                // For arrays, create a container element
                val rootElement = document.createElement("root")
                document.appendChild(rootElement)

                // Each array item gets an "item" element
                node.elements.forEachIndexed { index, element ->
                    val itemElement = document.createElement("item")
                    itemElement.setAttribute("index", index.toString())
                    appendValueToElement(document, itemElement, element)
                    rootElement.appendChild(itemElement)
                }
            }
            else -> {
                // For primitive values, create a simple root with value
                val rootElement = document.createElement("root")
                document.appendChild(rootElement)
                rootElement.textContent = getStringValue(node)
            }
        }

        return document
    }

    private fun findRootElementName(node: DataNode.ObjectNode): String {
        // Try to find a meaningful name for the root element
        val possibleNames = listOf("root", "document", "data")

        // Check if any of these keys exist at the top level
        for (name in possibleNames) {
            if (node.properties.containsKey(name)) {
                return name
            }
        }

        // Default to "root"
        return "root"
    }

    private fun appendNodeToElement(document: Document, parent: Element, key: String, node: DataNode) {
        if (key.startsWith("@")) {
            // Handle attributes (keys starting with @)
            parent.setAttribute(key.substring(1), getStringValue(node))
        } else {
            // Handle regular elements
            val element = document.createElement(key)
            appendValueToElement(document, element, node)
            parent.appendChild(element)
        }
    }

    private fun appendValueToElement(document: Document, element: Element, node: DataNode) {
        when (node) {
            is DataNode.ObjectNode -> {
                // Handle object properties
                node.properties.forEach { (key, value) ->
                    appendNodeToElement(document, element, key, value)
                }
            }
            is DataNode.ArrayNode -> {
                // Handle array items
                node.elements.forEachIndexed { index, item ->
                    val itemElement = document.createElement("item")
                    itemElement.setAttribute("index", index.toString())
                    appendValueToElement(document, itemElement, item)
                    element.appendChild(itemElement)
                }
            }
            else -> {
                // Handle primitive values
                element.textContent = getStringValue(node)
            }
        }
    }

    private fun getStringValue(node: DataNode): String {
        return when (node) {
            is DataNode.StringValue -> node.value
            is DataNode.NumberValue -> node.value.toString()
            is DataNode.BooleanValue -> node.value.toString()
            is DataNode.NullValue -> ""
            else -> ""
        }
    }
}