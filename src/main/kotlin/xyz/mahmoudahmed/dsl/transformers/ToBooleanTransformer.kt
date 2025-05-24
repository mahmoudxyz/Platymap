package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert a value to a boolean.
 */
class ToBooleanTransformer : ValueTransformer {
    private val trueValues = setOf("true", "yes", "y", "1", "on", "enabled", "active")

    override fun transform(value: Any): Any {
        val bool = when (value) {
            is DataNode.BooleanValue -> value.value
            is DataNode.StringValue -> value.value.lowercase() in trueValues
            is DataNode.NumberValue -> value.value.toInt() != 0
            is Boolean -> value
            is String -> value.lowercase() in trueValues
            is Number -> value.toInt() != 0
            else -> false
        }

        return DataNode.BooleanValue(bool)
    }
}