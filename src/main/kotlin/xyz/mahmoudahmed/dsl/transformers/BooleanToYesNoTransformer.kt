package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert a boolean value to "Yes" or "No".
 */
class BooleanToYesNoTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val bool = extractBooleanValue(value)
        val result = if (bool) "Yes" else "No"

        return when (value) {
            is DataNode.BooleanValue -> DataNode.StringValue(result)
            is DataNode -> DataNode.StringValue(result)
            else -> result
        }
    }
}