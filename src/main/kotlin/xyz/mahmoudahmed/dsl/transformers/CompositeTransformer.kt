package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Base class for transformers that compose multiple transformers.
 */
class CompositeTransformer(private val transformers: List<ValueTransformer>) : ValueTransformer {
    override fun transform(value: Any): Any {
        var result = value
        for (transformer in transformers) {
            result = transformer.transform(result)
        }
        return result
    }
}

/**
 * Helper function to extract string value from various types.
 */
fun extractStringValue(value: Any): String {
    return when (value) {
        is DataNode.StringValue -> value.value
        is DataNode -> value.asString ?: ""
        else -> value.toString()
    }
}

/**
 * Helper function to extract number value from various types.
 * Safely handles non-numeric strings and edge cases.
 */
fun extractNumberValue(value: Any): Double {
    return when (value) {
        is DataNode.NumberValue -> value.value.toDouble()
        is DataNode.StringValue -> {
            // Safely attempt to parse string as number
            value.value.toDoubleOrNull() ?: 0.0
        }
        is DataNode.BooleanValue -> {
            // Convert boolean to numeric: true = 1.0, false = 0.0
            if (value.value) 1.0 else 0.0
        }
        is DataNode -> {
            // Handle other DataNode types via asDouble method with fallback
            value.asDouble ?: 0.0
        }
        is Number -> value.toDouble()
        is Boolean -> if (value) 1.0 else 0.0
        is String -> {
            // Safely attempt to parse string as number
            value.toDoubleOrNull() ?: 0.0
        }
        else -> {
            // Last resort: try to convert toString() to number, fallback to 0.0
            value.toString().toDoubleOrNull() ?: 0.0
        }
    }
}

/**
 * Helper function to extract boolean value from various types.
 */
fun extractBooleanValue(value: Any): Boolean {
    return when (value) {
        is DataNode.BooleanValue -> value.value
        is DataNode -> value.asBoolean ?: false
        is Boolean -> value
        is String -> value.lowercase() == "true" || value == "1"
        is Number -> value.toInt() != 0
        else -> false
    }
}

/**
 * Helper function to create a DataNode from a value.
 */
fun toDataNode(value: Any): DataNode {
    return when (value) {
        is DataNode -> value
        is String -> DataNode.StringValue(value)
        is Number -> DataNode.NumberValue(value)
        is Boolean -> DataNode.BooleanValue(value)
        is Map<*, *> -> {
            val node = DataNode.ObjectNode()
            value.forEach { (k, v) ->
                if (k is String) {
                    node[k] = toDataNode(v ?: "")
                }
            }
            node
        }

        is List<*> -> {
            val node = DataNode.ArrayNode()
            value.forEach { v ->
                node.add(toDataNode(v ?: ""))
            }
            node
        }

        else -> DataNode.StringValue(value.toString())
    }
}