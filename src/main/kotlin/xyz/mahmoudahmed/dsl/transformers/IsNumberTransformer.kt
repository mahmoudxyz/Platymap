package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to check if a value is a number.
 */
class IsNumberTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val isNumber = when (value) {
            is DataNode.NumberValue -> true
            is DataNode.StringValue -> value.value.toDoubleOrNull() != null
            is Number -> true
            is String -> value.toDoubleOrNull() != null
            else -> false
        }

        return DataNode.BooleanValue(isNumber)
    }
}