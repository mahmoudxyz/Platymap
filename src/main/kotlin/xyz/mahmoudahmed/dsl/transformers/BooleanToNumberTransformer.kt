package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode


/**
 * Transformer to convert a boolean value to a number (1 or 0).
 */
class BooleanToNumberTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val bool = extractBooleanValue(value)
        val result = if (bool) 1 else 0

        return when (value) {
            is DataNode.BooleanValue -> DataNode.NumberValue(result)
            is DataNode -> DataNode.NumberValue(result)
            else -> result
        }
    }
}