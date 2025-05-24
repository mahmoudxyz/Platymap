package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert a boolean value to "Enabled" or "Disabled".
 */
class BooleanToEnabledDisabledTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val bool = extractBooleanValue(value)
        val result = if (bool) "Enabled" else "Disabled"

        return when (value) {
            is DataNode.BooleanValue -> DataNode.StringValue(result)
            is DataNode -> DataNode.StringValue(result)
            else -> result
        }
    }
}