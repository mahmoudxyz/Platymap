package xyz.mahmoudahmed.dsl.transformers

import xyz.mahmoudahmed.adapter.DataNode

/**
 * Transformer to add a value to a number.
 */
class AddTransformer(private val amount: Number) : ValueTransformer {
    override fun transform(value: Any): Any {
        val number = extractNumberValue(value)
        val result = number + amount.toDouble()

        return when (value) {
            is DataNode.NumberValue -> DataNode.NumberValue(result)
            is DataNode -> DataNode.NumberValue(result)
            else -> result
        }
    }
}

