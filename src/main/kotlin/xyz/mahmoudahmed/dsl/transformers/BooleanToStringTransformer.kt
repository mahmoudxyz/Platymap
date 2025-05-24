package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert a boolean value to a string.
 */
class BooleanToStringTransformer(
    private val trueValue: String = "true",
    private val falseValue: String = "false"
) : ValueTransformer {
    override fun transform(value: Any): Any {
        val bool = extractBooleanValue(value)
        val result = if (bool) trueValue else falseValue

        return when (value) {
            is DataNode.BooleanValue -> DataNode.StringValue(result)
            is DataNode -> DataNode.StringValue(result)
            else -> result
        }
    }
}
