package xyz.mahmoudahmed.adapter.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import xyz.mahmoudahmed.adapter.*
import xyz.mahmoudahmed.format.Format
import java.io.File
import java.io.InputStream

class JsonInputAdapter : InputAdapter {
    private val objectMapper = ObjectMapper()

    override fun canHandle(format: Format): Boolean =
        format == Format.JSON

    override fun parse(data: ByteArray): DataNode =
        parseJsonNode(objectMapper.readTree(data))

    override fun parse(content: String): DataNode =
        parseJsonNode(objectMapper.readTree(content))

    override fun parse(file: File): DataNode =
        parseJsonNode(objectMapper.readTree(file))

    override fun parse(inputStream: InputStream): DataNode =
        parseJsonNode(objectMapper.readTree(inputStream))

    private fun parseJsonNode(node: JsonNode): DataNode = when (node) {
        is ObjectNode -> {
            val objectNode = DataNode.ObjectNode()
            node.fields().forEach { (key, value) ->
                objectNode[key] = parseJsonNode(value)
            }
            objectNode
        }

        is ArrayNode -> {
            val arrayNode = DataNode.ArrayNode()
            node.forEach { element ->
                arrayNode.add(parseJsonNode(element))
            }
            arrayNode
        }

        is TextNode -> DataNode.StringValue(node.textValue())
        is IntNode -> DataNode.NumberValue(node.intValue())
        is LongNode -> DataNode.NumberValue(node.longValue())
        is DoubleNode -> DataNode.NumberValue(node.doubleValue())
        is FloatNode -> DataNode.NumberValue(node.floatValue())
        is BooleanNode -> DataNode.BooleanValue(node.booleanValue())
        is NullNode -> DataNode.NullValue

        else -> {
            // Handle unexpected node types
            DataNode.StringValue(node.toString())
        }
    }
}