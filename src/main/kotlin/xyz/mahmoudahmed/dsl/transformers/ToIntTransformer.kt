package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to convert a value to an integer.
 */
class ToIntTransformer : ValueTransformer {
    override fun transform(value: Any): Any {
        val num = when (value) {
            is DataNode.NumberValue -> value.value.toInt()
            is DataNode.StringValue -> value.value.toIntOrNull() ?: 0
            is DataNode.BooleanValue -> if (value.value) 1 else 0
            is Number -> value.toInt()
            is String -> value.toIntOrNull() ?: 0
            is Boolean -> if (value) 1 else 0
            else -> 0
        }

        return DataNode.NumberValue(num)
    }
}
