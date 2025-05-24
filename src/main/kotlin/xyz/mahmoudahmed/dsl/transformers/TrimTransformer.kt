package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode


/**
 * Transformer to trim whitespace from beginning and end of text.
 */
class TrimTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(str.trim())
            is DataNode -> DataNode.StringValue(str.trim())
            else -> str.trim()
        }
    }
}
