package xyz.mahmoudahmed.adapter.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import xyz.mahmoudahmed.adapter.DataNode
import xyz.mahmoudahmed.adapter.OutputAdapter
import xyz.mahmoudahmed.format.Format
import java.io.OutputStream
import java.io.Writer

class JsonOutputAdapter : OutputAdapter {
    private val objectMapper = ObjectMapper()

    override fun canHandle(format: Format): Boolean =
        format == Format.JSON

    override fun serialize(node: DataNode): String {
        val jsonNode = convertToJsonNode(node)
        return objectMapper.writeValueAsString(jsonNode)
    }

    override fun serialize(node: DataNode, writer: Writer) {
        val jsonNode = convertToJsonNode(node)
        objectMapper.writeValue(writer, jsonNode)
    }

    override fun serialize(node: DataNode, outputStream: OutputStream) {
        val jsonNode = convertToJsonNode(node)
        objectMapper.writeValue(outputStream, jsonNode)
    }

    private fun convertToJsonNode(node: DataNode): Any? {
        return when (node) {
            is DataNode.ObjectNode -> {
                val objectNode = objectMapper.createObjectNode()
                node.properties.forEach { (key, value) ->
                    addValueToObjectNode(objectNode, key, convertToJsonNode(value))
                }
                objectNode
            }
            is DataNode.ArrayNode -> {
                val arrayNode = objectMapper.createArrayNode()
                node.elements.forEach { element ->
                    addValueToArrayNode(arrayNode, convertToJsonNode(element))
                }
                arrayNode
            }
            is DataNode.StringValue -> node.value
            is DataNode.NumberValue -> node.value
            is DataNode.BooleanValue -> node.value
            is DataNode.NullValue -> null
        }
    }

    private fun addValueToObjectNode(objectNode: ObjectNode, key: String, value: Any?) {
        when (value) {
            null -> objectNode.putNull(key)
            is String -> objectNode.put(key, value)
            is Number -> {
                when (value) {
                    is Int -> objectNode.put(key, value)
                    is Long -> objectNode.put(key, value)
                    is Float -> objectNode.put(key, value)
                    is Double -> objectNode.put(key, value)
                    else -> objectNode.put(key, value.toDouble())
                }
            }
            is Boolean -> objectNode.put(key, value)
            is ObjectNode -> objectNode.set(key, value)
            is ArrayNode -> objectNode.set(key, value)
            else -> objectNode.put(key, value.toString())
        }
    }

    private fun addValueToArrayNode(arrayNode: ArrayNode, value: Any?) {
        when (value) {
            null -> arrayNode.addNull()
            is String -> arrayNode.add(value)
            is Number -> {
                when (value) {
                    is Int -> arrayNode.add(value)
                    is Long -> arrayNode.add(value)
                    is Float -> arrayNode.add(value)
                    is Double -> arrayNode.add(value)
                    else -> arrayNode.add(value.toDouble())
                }
            }
            is Boolean -> arrayNode.add(value)
            is ObjectNode -> arrayNode.add(value)
            is ArrayNode -> arrayNode.add(value)
            else -> arrayNode.add(value.toString())
        }
    }
}