package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert a string to a boolean value.
 */
class StringToBooleanTransformer : ValueTransformer {
    private val trueValues = setOf("true", "yes", "y", "1", "on", "enabled", "active")

    override fun transform(value: Any): Any {
        val str = extractStringValue(value).lowercase()
        val result = str in trueValues

        return when (value) {
            is DataNode.StringValue -> DataNode.BooleanValue(result)
            is DataNode -> DataNode.BooleanValue(result)
            else -> result
        }
    }
}