package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert a boolean value to "Active" or "Inactive".
 */
class BooleanToActiveInactiveTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val bool = extractBooleanValue(value)
        val result = if (bool) "Active" else "Inactive"

        return when (value) {
            is DataNode.BooleanValue -> DataNode.StringValue(result)
            is DataNode -> DataNode.StringValue(result)
            else -> result
        }
    }
}