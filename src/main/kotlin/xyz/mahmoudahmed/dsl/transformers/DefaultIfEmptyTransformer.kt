package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to provide a default value if the input is empty.
 */
class DefaultIfEmptyTransformer(private val defaultValue: String) : ValueTransformer {
    override fun transform(value: Any): Any {
        val isEmpty = when (value) {
            is DataNode.StringValue -> value.value.isEmpty()
            is DataNode.ArrayNode -> value.elements.isEmpty()
            is String -> value.isEmpty()
            is List<*> -> value.isEmpty()
            is Array<*> -> value.isEmpty()
            else -> false
        }

        return if (isEmpty) {
            when (value) {
                is DataNode.StringValue -> DataNode.StringValue(defaultValue)
                is DataNode -> DataNode.StringValue(defaultValue)
                else -> defaultValue
            }
        } else {
            value
        }
    }
}
