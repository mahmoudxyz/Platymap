package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert text to lowercase.
 */
class LowercaseTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(str.lowercase())
            is DataNode -> DataNode.StringValue(str.lowercase())
            else -> str.lowercase()
        }
    }
}