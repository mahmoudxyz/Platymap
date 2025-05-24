package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to replace empty with a specified value.
 */
class IfEmptyTransformer(private val replacement: DataNode) : ValueTransformer {
    override fun transform(value: Any): Any {
        val isEmpty = when (value) {
            is DataNode.StringValue -> value.value.isEmpty()
            is DataNode.ArrayNode -> value.elements.isEmpty()
            else -> false
        }

        return if (isEmpty) replacement else value
    }
}