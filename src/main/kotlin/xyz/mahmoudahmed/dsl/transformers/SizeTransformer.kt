package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to get the size of a collection.
 */
class SizeTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val size = when (value) {
            is DataNode.ArrayNode -> value.elements.size
            is DataNode.StringValue -> value.value.length
            is DataNode.ObjectNode -> value.properties.size
            is List<*> -> value.size
            is Array<*> -> value.size
            is Map<*, *> -> value.size
            is String -> value.length
            else -> 0
        }

        return when (value) {
            is DataNode -> DataNode.NumberValue(size)
            else -> size
        }
    }
}
