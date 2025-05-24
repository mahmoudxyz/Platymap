package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert a DataNode to a JSON string.
 */
class ToJsonStringTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        return try {
            // Use a JSON serializer to convert the DataNode to a string
            // This is a simplified implementation - in a real system you'd use a proper JSON serializer
            val mapper = com.fasterxml.jackson.databind.ObjectMapper()
            val jsonString = when (value) {
                is DataNode -> {
                    // Convert DataNode to a standard Map/List structure
                    val converted = convertDataNodeToStandard(value)
                    mapper.writeValueAsString(converted)
                }
                else -> mapper.writeValueAsString(value)
            }
            DataNode.StringValue(jsonString)
        } catch (e: Exception) {
            // Return a string representation if serialization fails
            DataNode.StringValue(value.toString())
        }
    }

    private fun convertDataNodeToStandard(node: DataNode): Any? {
        return when (node) {
            is DataNode.ObjectNode -> {
                node.properties.mapValues { (_, v) -> convertDataNodeToStandard(v) }
            }
            is DataNode.ArrayNode -> {
                node.elements.map { convertDataNodeToStandard(it) }
            }
            is DataNode.StringValue -> node.value
            is DataNode.NumberValue -> node.value
            is DataNode.BooleanValue -> node.value
            is DataNode.NullValue -> null
        }
    }
}
