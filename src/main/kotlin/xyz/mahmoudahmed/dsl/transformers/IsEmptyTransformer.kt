package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to check if a value is empty.
 */
class IsEmptyTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val isEmpty = when (value) {
            is DataNode.StringValue -> value.value.isEmpty()
            is DataNode.ArrayNode -> value.elements.isEmpty()
            is String -> value.isEmpty()
            is List<*> -> value.isEmpty()
            is Array<*> -> value.isEmpty()
            is Map<*, *> -> value.isEmpty()
            else -> false
        }

        return DataNode.BooleanValue(isEmpty)
    }
}