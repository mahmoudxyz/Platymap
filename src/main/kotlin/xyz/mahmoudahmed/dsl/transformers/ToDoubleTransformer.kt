package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert a value to a double.
 */
class ToDoubleTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val num = when (value) {
            is DataNode.NumberValue -> value.value.toDouble()
            is DataNode.StringValue -> value.value.toDoubleOrNull() ?: 0.0
            is DataNode.BooleanValue -> if (value.value) 1.0 else 0.0
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull() ?: 0.0
            is Boolean -> if (value) 1.0 else 0.0
            else -> 0.0
        }

        return DataNode.NumberValue(num)
    }
}
