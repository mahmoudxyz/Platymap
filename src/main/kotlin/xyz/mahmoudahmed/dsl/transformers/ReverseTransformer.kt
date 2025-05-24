package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to reverse a collection.
 */
class ReverseTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        return when (value) {
            is DataNode.ArrayNode -> {
                val reversed = DataNode.ArrayNode()
                value.elements.reversed().forEach { reversed.add(it) }
                reversed
            }
            is DataNode.StringValue -> {
                DataNode.StringValue(value.value.reversed())
            }
            else -> value
        }
    }
}