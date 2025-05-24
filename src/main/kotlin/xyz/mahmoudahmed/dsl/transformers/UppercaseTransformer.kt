package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert text to uppercase.
 */
class UppercaseTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val str = extractStringValue(value)
        return when (value) {
            is DataNode.StringValue -> DataNode.StringValue(str.uppercase())
            is DataNode -> DataNode.StringValue(str.uppercase())
            else -> str.uppercase()
        }
    }
}
